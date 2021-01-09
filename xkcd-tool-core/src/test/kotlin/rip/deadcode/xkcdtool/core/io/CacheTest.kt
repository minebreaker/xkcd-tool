package rip.deadcode.xkcdtool.core.io

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.google.common.truth.Truth8.assertThat
import org.junit.jupiter.api.Test
import rip.deadcode.xkcdtool.core.Toolbox
import rip.deadcode.xkcdtool.core.Toolbox.gson
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime


class CacheTest {

    fun prepareMockCacheFile() {
        Files.createDirectories(Toolbox.cachePath)
        Files.write(getIndexPath(), listOf(cacheEntryStr))
        Files.write(getJsonPath(xkcdJson.num), listOf(xkcdJsonStr))
    }

    @Test
    fun testWriteIndex() {
        mockPath()
        mockNet("")

        writeIndex(listOf(cacheEntry))

        val writtenFile = Files
            .readAllLines(getIndexPath())
            .map { gson.fromJson(it, IndexEntry::class.java) }

        assertThat(writtenFile).containsExactly(cacheEntry)
    }

    @Test
    fun testWriteIndexWhenFileAlreadyExists() {
        mockPath()
        mockNet("")
        Files.createDirectories(Toolbox.cachePath)
        Files.write(getIndexPath(), listOf("invalid"), StandardOpenOption.CREATE_NEW)

        val data = listOf(cacheEntry)
        writeIndex(data)

        val writtenFile = Files
            .readAllLines(getIndexPath())
            .map { gson.fromJson(it, IndexEntry::class.java) }

        assertThat(writtenFile).containsExactly(cacheEntry)
    }

    @Test
    fun testReadIndexWhenCached() {
        mockPath()
        mockNet("")
        prepareMockCacheFile()

        val result = readIndex()

        val expected = IndexEntry(cacheEntry.id, cacheEntry.title)
        assertWithMessage("In-memory cache is set").that(indexCache.get().index).containsExactly(expected)
        assertThat(result).containsExactly(expected)
    }

    @Test
    fun testReadIndexWhenNotCached() {
        mockPath()
        mockNet(html)

        val result = readIndex()

        val expected = IndexEntry(1, "Barrel - Part 1")
        assertWithMessage("In-memory cache is set")
            .that(indexCache.get().index).contains(expected)

        val writtenFile = Files
            .readAllLines(getIndexPath())
            .map { gson.fromJson(it, IndexEntry::class.java) }

        assertWithMessage("Index is written").that(writtenFile).contains(IndexEntry(1, "Barrel - Part 1"))
        assertThat(result).contains(expected)
    }

    @Test
    fun testReadJsonWhenCached() {
        mockPath()
        mockNet("")
        prepareMockCacheFile()

        val result = readJson(xkcdJson.num)

        assertThat(result).hasValue(xkcdJson)
    }

    @Test
    fun testReadJsonWhenNotCached() {
        mockPath()
        mockNet(xkcdJsonStr)

        val result = readJson(xkcdJson.num)

        assertThat(result).hasValue(xkcdJson)
    }

    @Test
    fun getCachedIndexWhenCached() {
        mockPath()
        mockNet("")
        prepareMockCacheFile()
        val mockNow = ZonedDateTime.parse("2000-01-01T00:00:00+00:00")
        Toolbox.clock = Clock.fixed(mockNow.toInstant(), ZoneOffset.UTC)

        val cache = listOf(IndexEntry(308, "Interesting Life"))

        indexCache.set(IndexCache(cache, mockNow.minusMinutes(1)))

        val result = getCachedIndex()

        assertThat(result).isEqualTo(cache)
    }

    @Test
    fun getCachedIndexWhenNotCached() {
        mockPath()
        mockNet("")
        prepareMockCacheFile()
        val mockNow = ZonedDateTime.parse("2000-01-01T00:00:00+00:00")
        Toolbox.clock = Clock.fixed(mockNow.toInstant(), ZoneOffset.UTC)
        indexCache.set(IndexCache(listOf(), mockNow.minusMinutes(1)))

        val result = getCachedIndex()

        assertThat(result).containsExactly(IndexEntry(cacheEntry.id, cacheEntry.title))
    }

    @Test
    fun getCachedIndexWhenCachedButExpired() {
        mockPath()
        mockNet("")
        prepareMockCacheFile()
        Toolbox.clock = Clock.fixed(ZonedDateTime.parse("2000-01-01T00:00:00+00:00").toInstant(), ZoneOffset.UTC)
        indexCache.set(
            IndexCache(
                listOf(IndexEntry(308, "Interesting Life")),
                ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
            )
        )

        val result = getCachedIndex()

        assertThat(result).containsExactly(IndexEntry(cacheEntry.id, cacheEntry.title))
    }

    @Test
    fun getCachedJsonWhenCached() {
        mockPath()
        mockNet("")
        prepareMockCacheFile()

        jsonCache.put(1, xkcdJson)  // Uses a dummy id to ensure the result is read from the cache

        val result = getCachedJson(1)

        assertThat(result).hasValue(xkcdJson)
    }


    @Test
    fun getCachedJsonWhenNotCached() {
        mockPath()
        mockNet("")
        prepareMockCacheFile()
        jsonCache.invalidateAll()

        val result = getCachedJson(xkcdJson.num)

        assertThat(result).hasValue(xkcdJson)
    }
}
