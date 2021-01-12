package rip.deadcode.xkcdtool.cli


// Since Graal doesn't support awt,
// we have to manually open the browser.
// https://stackoverflow.com/a/28807079/9506289
fun openBrowser(url: String) {
    val os = System.getProperty("os.name").toLowerCase()

    when {
        os.contains("win") -> {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler $url")
        }
        os.contains("mac") -> {
            Runtime.getRuntime().exec("open $url")
        }
        // Assume it's linux
        else -> {
            Runtime.getRuntime().exec("sh -c firefox $url")
        }
    }
}
