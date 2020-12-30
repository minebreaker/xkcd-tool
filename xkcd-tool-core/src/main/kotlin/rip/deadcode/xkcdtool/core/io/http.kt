package rip.deadcode.xkcdtool.core.io

import com.google.api.client.http.GenericUrl
import org.jsoup.Jsoup
import rip.deadcode.xkcdtool.core.Toolbox.baseUrl
import rip.deadcode.xkcdtool.core.Toolbox.gson
import rip.deadcode.xkcdtool.core.Toolbox.httpClient
import rip.deadcode.xkcdtool.core.Toolbox.indexUrl
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.UncheckedIOException
import java.nio.charset.StandardCharsets
import java.util.*


fun requestIndex(): List<IndexEntry> {
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

fun requestJson(url: String): Optional<XkcdJson> {
    return try {
        val response = httpClient.createRequestFactory()
            .buildGetRequest(GenericUrl(url))
            .execute()

        // TODO: status check

        Optional.of(
            gson.fromJson(
                InputStreamReader(BufferedInputStream(response.content), StandardCharsets.UTF_8),
                XkcdJson::class.java
            )
        )

    } catch (e: IOException) {
        Optional.empty()
    }
}
