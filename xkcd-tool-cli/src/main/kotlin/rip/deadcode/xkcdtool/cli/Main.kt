package rip.deadcode.xkcdtool.cli

import rip.deadcode.xkcdtool.core.OutputType
import rip.deadcode.xkcdtool.core.askUrl


object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val config = parseArg(args)

        if (config.query.isEmpty()) {
            println("Usage: xkcd [comic name] (flags)")
            return
        }

        val mode = if (config.explainMode) OutputType.EXPLAIN else OutputType.NORMAL

        val possibleUrl = askUrl(config.query, mode)
        if (possibleUrl.isPresent) {
            val url = possibleUrl.get()
            if (config.urlMode) {
                println(url)
            } else {
                openBrowser(url)
            }
        } else {
            println("No matched comic found.")
        }
    }
}
