package rip.deadcode.xkcdtool.core

import rip.deadcode.xkcdtool.core.OutputType.EXPLAIN
import rip.deadcode.xkcdtool.core.OutputType.IMAGE
import rip.deadcode.xkcdtool.core.OutputType.JSON
import rip.deadcode.xkcdtool.core.OutputType.NORMAL
import java.util.*


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

enum class OutputType {
    NORMAL,
    EXPLAIN,
    IMAGE,
    JSON
}

fun askUrl(query: List<String>, output: OutputType): Optional<String> {

    val possibleId = isId(query)
    return when {
        isRandom(query) -> Optional.of(
            when (output) {
                NORMAL -> Toolbox.randomUrl
                EXPLAIN -> Toolbox.explainRandomUrl
                IMAGE -> TODO()
                JSON -> TODO()
            }
        )
        isLatest(query) -> Optional.of(
            when (output) {
                // We just use base url for the latest comic
                NORMAL -> Toolbox.baseUrl
                EXPLAIN -> Toolbox.explainBaseUrl
                IMAGE -> TODO()
                JSON -> TODO()
            }
        )
        else -> {
            possibleId
                .flatMap { id ->
                    if (output == JSON) {
                        // // If the output is json, just convert the given id to the url
                        // TODO: JSON or JSON url?
                        Optional.of(idToJsonUrl(id))
                        TODO()
                    } else {
                        // Otherwise, make sure the id exists
                        askJsonFromId(id).map { json ->
                            when (output) {
                                NORMAL -> idToComicUrl(id)
                                EXPLAIN -> idToExplainUrl(id)
                                IMAGE -> json.img
                                JSON -> throw Error("Unreachable")
                            }
                        }
                    }
                }
                .or {
                    val index = askIndex()
                    match(query, index.stream()) { e -> regularize(e.rawTitle) }.map { matched ->
                        when (output) {
                            NORMAL -> idToComicUrl(matched.id)
                            EXPLAIN -> idToExplainUrl(matched.id)
                            IMAGE -> {
                                askJsonFromId(matched.id).map { json -> json.img }
                                    .orElseThrow()  // TODO: what if query is matched but json is not found?
                            }
                            JSON -> TODO()
                        }
                    }
                }
        }
    }
}

fun <T : Any> OptionalInt.map(f: (Int) -> T) = if (this.isPresent) {
    Optional.of(f(this.asInt))
} else {
    Optional.empty()
}

fun <T : Any> OptionalInt.flatMap(f: (Int) -> Optional<T>) = if (this.isPresent) {
    f(this.asInt)
} else {
    Optional.empty()
}
