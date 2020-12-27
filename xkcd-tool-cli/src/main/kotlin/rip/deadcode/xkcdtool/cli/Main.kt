package rip.deadcode.xkcdtool.cli

import rip.deadcode.xkcdtool.core.Toolbox.baseUrl
import rip.deadcode.xkcdtool.core.Toolbox.explainBaseUrl
import rip.deadcode.xkcdtool.core.Toolbox.explainRandomUrl
import rip.deadcode.xkcdtool.core.Toolbox.randomUrl
import rip.deadcode.xkcdtool.core.askIndex
import rip.deadcode.xkcdtool.core.askJsonFromId
import rip.deadcode.xkcdtool.core.idToComicUrl
import rip.deadcode.xkcdtool.core.idToExplainUrl
import rip.deadcode.xkcdtool.core.match
import rip.deadcode.xkcdtool.core.regularize
import java.util.*


object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val config = parseArg(args)

        if (config.query.isEmpty()) {
            println("Usage: xkcd [comic name] (flags)")
            return
        }

        val possibleId = isId(config.query)
        val url: String = when {
            isRandom(config.query) -> {
                if (config.explainMode) {
                    explainRandomUrl
                } else {
                    randomUrl
                }
            }
            isLatest(config.query) -> {
                // We just use base url for the latest comic
                if (config.explainMode) {
                    explainBaseUrl
                } else {
                    baseUrl
                }
            }
            else -> {
                if (possibleId.isPresent) {
                    val id = possibleId.asInt
                    val _json = askJsonFromId(id)  // Make sure the id exists
                    // TODO: if 404 fallback to index search
                    if (config.explainMode) {
                        idToExplainUrl(id)
                    } else {
                        idToComicUrl(id)
                    }
                } else {
                    val index = askIndex()

                    val matchedOptional = match(config.query, index.stream()) { e -> regularize(e.rawTitle) }
                    if (matchedOptional.isEmpty) {
                        println("No matched comic found.")
                        return
                    }
                    val matched = matchedOptional.get()

                    if (config.explainMode) {
                        idToExplainUrl(matched.id)
                    } else {
                        idToComicUrl(matched.id)
                    }
                }
            }
        }

        if (config.urlMode) {
            println(url)
        } else {
            openBrowser(url)
        }
    }
}

fun isRandom(query: List<String>) = query.size == 1 && query.first() == "random"
fun isLatest(query: List<String>) = query.size == 1 && query.first() == "latest"
fun isId(query: List<String>): OptionalInt {
    return if (query.size == 1) {
        val i = query.first().toIntOrNull()
        if (i == null) {
            OptionalInt.empty()
        } else {
            OptionalInt.of(i)
        }
    } else {
        OptionalInt.empty()
    }
}
