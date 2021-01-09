package rip.deadcode.xkcdtool.core

import com.google.common.truth.Truth8.assertThat
import org.junit.jupiter.api.Test
import rip.deadcode.xkcdtool.core.OutputType.EXPLAIN
import rip.deadcode.xkcdtool.core.OutputType.IMAGE
import rip.deadcode.xkcdtool.core.OutputType.JSON
import rip.deadcode.xkcdtool.core.OutputType.NORMAL
import rip.deadcode.xkcdtool.core.io.IndexCache
import rip.deadcode.xkcdtool.core.io.IndexEntry
import rip.deadcode.xkcdtool.core.io.idToComicUrl
import rip.deadcode.xkcdtool.core.io.idToExplainUrl
import rip.deadcode.xkcdtool.core.io.idToJsonUrl
import rip.deadcode.xkcdtool.core.io.indexCache
import rip.deadcode.xkcdtool.core.io.jsonCache
import rip.deadcode.xkcdtool.core.io.mockNet
import rip.deadcode.xkcdtool.core.io.mockPath
import rip.deadcode.xkcdtool.core.io.xkcdJson
import java.time.Clock
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*


class UtilsTest {

    @Test
    fun testAskUrl() {
        mockNet("")
        mockPath()
        Toolbox.random = Random(0)

        val mockNow = ZonedDateTime.parse("2000-01-01T00:00:00+00:00")
        Toolbox.clock = Clock.fixed(mockNow.toInstant(), ZoneOffset.UTC)
        indexCache.set(IndexCache(listOf(IndexEntry(xkcdJson.num, xkcdJson.title)), mockNow))

        jsonCache.put(xkcdJson.num, xkcdJson)

        val randomNormal = askUrl(listOf("random"), NORMAL)
        assertThat(randomNormal).hasValue(Toolbox.randomUrl)

        val randomExplain = askUrl(listOf("random"), EXPLAIN)
        assertThat(randomExplain).hasValue(Toolbox.explainRandomUrl)

        val randomImage = askUrl(listOf("random"), IMAGE)
        assertThat(randomImage).hasValue(xkcdJson.img)

        val randomJson = askUrl(listOf("random"), JSON)
        assertThat(randomJson).hasValue(idToJsonUrl(xkcdJson.num))

        val latestNormal = askUrl(listOf("latest"), NORMAL)
        assertThat(latestNormal).hasValue(Toolbox.baseUrl)

        val latestExplain = askUrl(listOf("latest"), EXPLAIN)
        assertThat(latestExplain).hasValue(Toolbox.explainBaseUrl)

        val idNormal = askUrl(listOf(xkcdJson.num.toString()), NORMAL)
        assertThat(idNormal).hasValue(idToComicUrl(xkcdJson.num))

        val idExplain = askUrl(listOf(xkcdJson.num.toString()), EXPLAIN)
        assertThat(idExplain).hasValue(idToExplainUrl(xkcdJson.num))

        val idImage = askUrl(listOf(xkcdJson.num.toString()), IMAGE)
        assertThat(idImage).hasValue(xkcdJson.img)

        val idJson = askUrl(listOf(xkcdJson.num.toString()), JSON)
        assertThat(idJson).hasValue(idToJsonUrl(xkcdJson.num))

        val queryNormal = askUrl(listOf("interesting", "life"), NORMAL)
        assertThat(queryNormal).hasValue(idToComicUrl(xkcdJson.num))

        val queryExplain = askUrl(listOf("interesting", "life"), EXPLAIN)
        assertThat(queryExplain).hasValue(idToExplainUrl(xkcdJson.num))

        val queryImage = askUrl(listOf("interesting", "life"), IMAGE)
        assertThat(queryImage).hasValue(xkcdJson.img)

//        val queryJson = askUrl(listOf("interesting", "life"), JSON)
//        assertThat(queryJson).hasValue(idToJsonUrl(xkcdJson.num))
    }
}
