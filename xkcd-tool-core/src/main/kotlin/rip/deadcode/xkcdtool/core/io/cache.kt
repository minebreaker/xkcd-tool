package rip.deadcode.xkcdtool.core.io

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import rip.deadcode.xkcdtool.core.Toolbox
import rip.deadcode.xkcdtool.core.Toolbox.gson
import rip.deadcode.xkcdtool.core.internal.get
import java.nio.file.Files
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.DSYNC
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicReference

/*
 * xkcd-tool will cache indexes of comics in a `~/.xkcd-tool/` directory.
 *
 * A `index` file is a JSON-L formatted for each line is a metadata of the comics.
 * The metadata JSON format is represented as `IndexEntry`.
 *
 * A file named with numbers is a JSON of the comic which file name is an id of the comic it represent.
 * Format is `XkcdJson`.
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
val jsonCache: Cache<Int, XkcdJson> = CacheBuilder
    .newBuilder()
    .build()

fun getIndexPath() = Toolbox.cachePath.resolve("index")
fun getJsonPath(id: Int) = Toolbox.cachePath.resolve(id.toString())


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
    val json = jsonCache.get(id)

    return json.or {
        val newJson = readJson(id)
        newJson.ifPresent {
            jsonCache.put(it.num, it)
        }
        newJson
    }
}


fun readIndex(): List<IndexEntry> {

    val lines = if (Files.exists(getIndexPath())) {
        Files.readAllLines(getIndexPath())
    } else {
        listOf()
    }

    val index = lines.map { gson.fromJson(it, IndexEntry::class.java) }

    // 1. Read index
    // TODO: check index date expiration

    val newIndex = if (index.isNotEmpty()) {
        index
    } else {
        // Download index if not cached
        val newIndex = requestIndex()
        writeIndex(newIndex)
        newIndex
    }

    indexCache.set(IndexCache(newIndex, ZonedDateTime.now()))

    return newIndex
}

fun readJson(id: Int): Optional<XkcdJson> {

    val path = getJsonPath(id)

    return if (Files.exists(path)) {
        // TODO catch Exception
        val str = Files.readString(path)
        val json = gson.fromJson(str, XkcdJson::class.java)
        Optional.of(json)
    } else {
        // Download json if not cached
        val newJson = requestJson(idToJsonUrl(id))
        newJson.ifPresent {
            writeJson(it)
        }
        newJson
    }
}

val options = arrayOf(CREATE, WRITE, TRUNCATE_EXISTING, DSYNC)

fun writeIndex(index: List<IndexEntry>) {
    Files.createDirectories(Toolbox.cachePath)
    Files.write(getIndexPath(), index.map { gson.toJson(it) }, *options)
}

fun writeJson(json: XkcdJson) {
    Files.createDirectories(Toolbox.cachePath)
    Files.writeString(getJsonPath(json.num), gson.toJson(json), *options)
}
