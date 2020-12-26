package rip.deadcode.xkcdtool.cli

import rip.deadcode.xkcdtool.core.askIndex
import rip.deadcode.xkcdtool.core.idToComicUrl
import rip.deadcode.xkcdtool.core.idToExplainUrl
import rip.deadcode.xkcdtool.core.match
import rip.deadcode.xkcdtool.core.regularize
import java.awt.Desktop
import java.net.URI

object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val config = parseArg(args)

        val index = askIndex()

        val matchedOptional = match(config.name, index.stream()) { e -> regularize(e.rawTitle) }
        if (matchedOptional.isEmpty) {
            println("No matched comic found.")
            return
        }
        val matched = matchedOptional.get()

//        val json = askJsonFromId(matched.id)

        val url = if (config.explainMode) {
            idToExplainUrl(matched.id)
        } else {
            idToComicUrl(matched.id)
        }

        if (config.urlMode) {
            println(url)
        } else {
            Desktop.getDesktop().browse(URI(url))
        }
    }
}
