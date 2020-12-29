package rip.deadcode.xkcdtool.core

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.gson.Gson
import java.nio.file.FileSystems
import java.util.*


object Toolbox {
    var fileSystem = FileSystems.getDefault()
    val cachePath = fileSystem.getPath("~/xkcd-tool/index").normalize().toAbsolutePath()

    var httpClient: HttpTransport = NetHttpTransport()
    var baseUrl = "https://xkcd.com/"
    val indexUrl = "${baseUrl}archive/"
    val latestJsonUrl = "${baseUrl}info.0.json"
    var randomUrl = "https://c.xkcd.com/random/comic/"

    var explainBaseUrl = "https://www.explainxkcd.com/"
    val explainRandomUrl = "${explainBaseUrl}wiki/index.php/Special:Random"

    var gson = Gson()
    var random = Random()
}
