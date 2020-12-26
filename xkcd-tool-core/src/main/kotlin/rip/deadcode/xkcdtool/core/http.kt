package rip.deadcode.xkcdtool.core

import com.google.api.client.http.GenericUrl
import org.jsoup.Jsoup
import rip.deadcode.xkcdtool.core.Toolbox.baseUrl
import rip.deadcode.xkcdtool.core.Toolbox.explainBaseUrl
import rip.deadcode.xkcdtool.core.Toolbox.gson
import rip.deadcode.xkcdtool.core.Toolbox.httpClient
import rip.deadcode.xkcdtool.core.Toolbox.indexUrl
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.UncheckedIOException
import java.nio.charset.StandardCharsets


/**
 * A model for xkcd json interface which can be retrieved by
 * `http://xkcd.com/${id}/info.0.json`.
 * There's little canonical documentations,
 * so the following description is mostly my guess.
 *
 * @param num Number of the comic. In xkcd-tool we call it `id` in other documentations.
 * @param link No idea. Mostly blank.
 * @param title Title of the comic.
 * @param safeTitle Not sure how 'safe' is it.
 * @param transcript Transcript.
 * @param img URL String of the image
 * @param alt Alt text of the comic
 * @param news No idea.
 * @param year A publish year
 * @param month A publish month
 * @param day A publish day
 */
data class XkcdJson(
    val num: Int,
    val link: String,
    val title: String,
    val safeTitle: String,
    val transcript: String,
    val img: String,
    val alt: String,
    val news: String,
    val year: String,
    val month: String,
    val day: String
)

data class IndexEntry(
    val id: Int,
    val rawTitle: String
)

fun askIndex(): List<IndexEntry> {
    try {
        val response = httpClient.createRequestFactory()
            .buildGetRequest(GenericUrl(indexUrl))
            .execute()

        // TODO: status check

        val document = Jsoup.parse(response.content, "UTF-8", baseUrl)  // FIXME
        val container = document.getElementById("middleContainer")
        val links = container.getElementsByTag("a")

        return links.map { link ->
            IndexEntry(
                link.attr("href").drop(1).dropLast(1).toInt(),
                link.text()
            )
        }
    } catch (e: IOException) {
        throw UncheckedIOException(e)  // FIXME
    }
}

fun askJsonFromId(id: Int): XkcdJson {
    try {
        val response = httpClient.createRequestFactory()
            .buildGetRequest(GenericUrl(idToJsonUrl(id)))
            .execute()

        // TODO: status check

        return gson.fromJson(
            InputStreamReader(BufferedInputStream(response.content), StandardCharsets.UTF_8),
            XkcdJson::class.java
        )

    } catch (e: IOException) {
        throw UncheckedIOException(e)  // FIXME
    }
}

fun idToJsonUrl(id: Int) = "${baseUrl}${id}/info.0.json"
fun idToComicUrl(id: Int) = "${baseUrl}${id}/"
fun idToExplainUrl(id: Int) = "${explainBaseUrl}wiki/index.php/${id}"
