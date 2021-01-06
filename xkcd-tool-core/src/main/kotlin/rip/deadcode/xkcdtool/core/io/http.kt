package rip.deadcode.xkcdtool.core.io

import org.jsoup.Jsoup
import rip.deadcode.xkcdtool.core.Toolbox.baseUrl
import rip.deadcode.xkcdtool.core.Toolbox.gson
import rip.deadcode.xkcdtool.core.Toolbox.httpClient
import rip.deadcode.xkcdtool.core.Toolbox.indexUrl
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.UncheckedIOException
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.*


fun requestIndex(): List<IndexEntry> {
    try {
        val response = httpClient
            .send(
                HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(URI(indexUrl))
                    .build(),
                HttpResponse.BodyHandlers.ofInputStream()
            )
            .body()

        // TODO: status check

        val document = Jsoup.parse(response, "UTF-8", baseUrl)  // FIXME
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
        val response = httpClient
            .send(
                HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(URI(url))
                    .build(),
                HttpResponse.BodyHandlers.ofInputStream()
            )
            .body()

        // TODO: status check

        Optional.of(
            gson.fromJson(
                InputStreamReader(BufferedInputStream(response), StandardCharsets.UTF_8),
                XkcdJson::class.java
            )
        )

    } catch (e: IOException) {
        Optional.empty()
    }
}
