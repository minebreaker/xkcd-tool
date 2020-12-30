package rip.deadcode.xkcdtool.core.io

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

/*
 * Internet => File Cache => In-Memory Cache
 */
fun getIndex(): List<IndexEntry> {
    return getCachedIndex()
}

fun getJson(id: Int): Optional<XkcdJson> {
    return getCachedJson(id)
}

fun mapToXkcdJson(map: Map<String, Any>): Optional<XkcdJson> {
    val num = map["num"]
    val link = map["link"]
    val title = map["title"]
    val safeTitle = map["safe_title"]
    val transcript = map["transcript"]
    val img = map["img"]
    val alt = map["alt"]
    val news = map["news"]
    val year = map["year"]
    val month = map["month"]
    val day = map["day"]

    return if (num is Int && link is String && title is String && safeTitle is String && transcript is String && img is String && alt is String &&
        news is String && year is String && month is String && day is String
    ) {
        Optional.of(XkcdJson(num, link, title, safeTitle, transcript, img, alt, news, year, month, day))
    } else {
        Optional.empty()
    }
}

fun idToJsonUrl(id: Int) = "${Toolbox.baseUrl}${id}/info.0.json"
fun idToComicUrl(id: Int) = "${Toolbox.baseUrl}${id}/"
fun idToExplainUrl(id: Int) = "${Toolbox.explainBaseUrl}wiki/index.php/${id}"
