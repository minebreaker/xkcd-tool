package rip.deadcode.xkcdtool.core.io

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.google.gson.Gson
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import rip.deadcode.xkcdtool.core.Toolbox
import java.io.InputStream
import java.net.http.HttpClient
import java.net.http.HttpResponse
import java.time.Clock
import java.time.Duration
import java.util.*


fun mockNet(responseBody: String) {
    Toolbox.httpClient = HttpClient
        .newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()


    val mockClient = Mockito.mock(HttpClient::class.java)
    Toolbox.httpClient = mockClient
    @Suppress("UNCHECKED_CAST")
    val mockResponse: HttpResponse<InputStream> = Mockito.mock(HttpResponse::class.java) as HttpResponse<InputStream>

    `when`(mockClient.send<InputStream>(Mockito.any(), Mockito.any())).thenReturn(mockResponse)
    `when`(mockResponse.body()).thenReturn(responseBody.byteInputStream())
}

fun mockPath() {
    Toolbox.cachePath = Jimfs.newFileSystem(Configuration.unix())
        .getPath("/user/home/exkcd-tool").normalize().toAbsolutePath()
}

/**
 * Simplified html taken from `/archive/`
 */
val html = """
        <!DOCTYPE html>
        <html>
        <body>
        <div id="middleContainer" class="box">
            <h1>Comics:</h1><br />
            (Hover mouse over title to view publication date)<br /><br />
            <a href="/3/" title="2006-1-1">Island (sketch)</a><br />
            <a href="/2/" title="2006-1-1">Petit Trees (sketch)</a><br />
            <a href="/1/" title="2006-1-1">Barrel - Part 1</a><br />
        </div>
        </body>
        </html>
    """.trimIndent()

val xkcdJson = XkcdJson(
    308,
    "",
    "Interesting Life",
    "Interesting Life",
    "[[On the left hand side of the panel is a cutaway of several floors of an office, in gray.  On the right side a blue sky with clouds, and green hills.  Hanging from a cable is a GIRL, clearly having rappelled down the side of the building]]\nGIRL: You know how some people consider \"May you have an interesting life\" to be a curse?\nGUY IN OFFICE: Yeah...\nGIRL: Fuck those people.  Wanna have an adventure?\n{{Alt-text: Quick, fashion a climbing harness out of a cat-6 cable and follow me down}}",
    "https://imgs.xkcd.com/comics/interesting_life.png",
    "Quick, fashion a climbing harness out of cat-6 cable and follow me down.",
    "",
    "2007",
    "8",
    "27"
)
val xkcdJsonStr = Toolbox.gson.toJson(xkcdJson)
val cacheEntry = IndexEntry(1168, "tar")
val cacheEntryStr = Toolbox.gson.toJson(cacheEntry)
