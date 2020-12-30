package rip.deadcode.xkcdtool.core.io

import com.google.common.cache.CacheBuilder
import rip.deadcode.xkcdtool.core.Toolbox
import rip.deadcode.xkcdtool.core.internal.get
import rip.deadcode.xkcdtool.core.internal.optional
import java.io.IOException
import java.nio.file.Files
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.streams.toList

/*
 * xkcd-tool will cache indexes of comics in a `~/.xkcd-tool/index` file.
 *
 * The file is a JSON-L formatted for each line is a metadata of the comics.
 * The metadata JSON format is either:
 *   * a metadata returned by the xkcd JSON format(represented by the class `XkcdJson`)
 *   * a cache entry represented by the class `CachedEntry`
 */


data class IndexCache(val index: List<IndexEntry>, val lastUpdated: ZonedDateTime)

/**
 * An internal index cache to reduce disk IO.
 */
val indexCache = AtomicReference(IndexCache(listOf(), ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)))

/**
 * An internal json cache to reduce disk IO.
 * This will never expire since returned JSON is hardly changing.
 * The key is an id of the entry.
 */
val jsonCache = CacheBuilder
    .newBuilder()
    .build<Int, XkcdJson>()


/**
 * Raw data of the cache.
 *
 * @param id Comic ID aka the `num` field of the comic
 * @param title Original title
 * @param url Url of the image (not comic itself)
 */
data class CachedEntry(
    val id: Int,
    val title: String,
    val url: String,
)

fun mapToCachedEntry(map: Map<String, Any>): Optional<CachedEntry> {
    val id = map["id"]
    val title = map["title"]
    val url = map["url"]

    return if (id is Int && title is String && url is String) {
        Optional.of(CachedEntry(id, title, url))
    } else {
        Optional.empty()
    }
}

fun getCachedIndex(): List<IndexEntry> {

    val (index, lastUpdated) = indexCache.get()
    val now = ZonedDateTime.now(Toolbox.clock)
    // If the cache is already filled, just returns them.
    if (lastUpdated.isBefore(now.minusDays(1)) && index.isNotEmpty()) {
        return index
    }

    // This update operation can run twice, but we don't care.
    return readIndex()
}

fun getCachedJson(id: Int): Optional<XkcdJson> {
    return jsonCache.get(id).or { readJson(id) }
}

fun readFile(): Pair<List<XkcdJson>, List<CachedEntry>> {
    val lines = Files.readAllLines(Toolbox.cachePath)

    @Suppress("UNCHECKED_CAST")
    val maps = lines.map { Toolbox.gson.fromJson(it, Map::class.java) as Map<String, Any> }

    val xkcdJsons = maps
        .stream()
        .filter { it.containsKey("num") }
        .flatMap { mapToXkcdJson(it).stream() }
        .toList()
    val cachedEntries = maps
        .stream()
        .filter { it.containsKey("id") }
        .flatMap { mapToCachedEntry(it).stream() }
        .toList()

    return xkcdJsons to cachedEntries
}

fun readIndex(): List<IndexEntry> {

    val (xkcdJsons, cachedEntries) = readFile()

    // 1. Update index
    val cachedIndex = xkcdJsons.map { IndexEntry(it.num, it.title) } + cachedEntries.map { IndexEntry(it.id, it.title) }

    val newIndex = if (cachedIndex.isNotEmpty()) {
        cachedIndex
    } else {
        // Download index if not cached
        val newIndex = requestIndex()
        writeCacheFile((xkcdJsons + cachedEntries))
        newIndex
    }

    indexCache.set(IndexCache(newIndex, ZonedDateTime.now()))

    // 2. Update json
    xkcdJsons.forEach {
        jsonCache.put(it.num, it)
    }

    return newIndex
}

fun readJson(id: Int): Optional<XkcdJson> {

    val (xkcdJsons, cachedEntries) = readFile()
    val json = xkcdJsons.firstOrNull().optional()

    return if (json.isPresent) {
        json
    } else {
        // Download json if not cached
        val newJson = requestJson(idToJsonUrl(id))

        if (newJson.isPresent) {
            val newJsonRaw = newJson.get()
            val newJsons = xkcdJsons.map { if (it.num == newJsonRaw.num) newJsonRaw else it }
            writeCacheFile((newJsons + cachedEntries))
        }

        newJson
    }
}

fun writeCacheFile(lines: List<Any>) {
    try {
        // TODO: Random write is better
        Files.write(Toolbox.cachePath, lines.map { Toolbox.gson.toJson(it) })
    } catch (e: IOException) {
        // Just ignore the error since cache write is not a fatal operation
        e.printStackTrace()
    }
}
