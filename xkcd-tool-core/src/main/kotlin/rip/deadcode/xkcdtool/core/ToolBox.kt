package rip.deadcode.xkcdtool.core

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.gson.Gson
import java.nio.file.FileSystems


object Toolbox {
    var fileSystem = FileSystems.getDefault()
    var cachePath = fileSystem.getPath("~/xkcd-tool/index").normalize().toAbsolutePath()

    var httpClient: HttpTransport = NetHttpTransport()
    var baseUrl = "https://xkcd.com/"
    var indexUrl = "${baseUrl}archive/"

    var explainBaseUrl = "https://www.explainxkcd.com/"

    var gson = Gson()
}
