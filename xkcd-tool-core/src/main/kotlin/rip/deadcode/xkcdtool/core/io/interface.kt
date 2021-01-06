package rip.deadcode.xkcdtool.core.io

import com.google.gson.annotations.SerializedName
import rip.deadcode.xkcdtool.core.Toolbox
import java.util.*


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
    @SerializedName("safe_title")
    val safeTitle: String,
    val transcript: String,
    val img: String,
    val alt: String,
    val news: String,
    val year: String,
    val month: String,
    val day: String
)

/**
 * Index data of the comic.
 * @param id Comic ID aka the `num` field of the comic
 * @param title Original title
 */
data class IndexEntry(
    val id: Int,
    val title: String
)

/*
 * Internet => File Cache => In-Memory Cache
 */
fun getIndex(): List<IndexEntry> {
    return getCachedIndex()
}

fun getJson(id: Int): Optional<XkcdJson> {
    return getCachedJson(id)
}

fun idToJsonUrl(id: Int) = "${Toolbox.baseUrl}${id}/info.0.json"
fun idToComicUrl(id: Int) = "${Toolbox.baseUrl}${id}/"
fun idToExplainUrl(id: Int) = "${Toolbox.explainBaseUrl}wiki/index.php/${id}"
