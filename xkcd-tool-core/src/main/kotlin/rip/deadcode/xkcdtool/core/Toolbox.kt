package rip.deadcode.xkcdtool.core

import com.google.gson.Gson
import java.net.http.HttpClient
import java.nio.file.Paths
import java.time.Clock
import java.time.Duration
import java.util.*


object Toolbox {

    var cachePath = Paths.get(System.getProperty("user.home"), ".xkcd-tool").normalize().toAbsolutePath()

    var baseUrl = "https://xkcd.com/"
    val indexUrl = "${baseUrl}archive/"
    val latestJsonUrl = "${baseUrl}info.0.json"
    var randomUrl = "https://c.xkcd.com/random/comic/"

    var explainBaseUrl = "https://www.explainxkcd.com/"
    val explainRandomUrl = "${explainBaseUrl}wiki/index.php/Special:Random"

    var httpClient = HttpClient
        .newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()
    var gson = Gson()
    var random = Random()
    var clock = Clock.systemDefaultZone()
}
