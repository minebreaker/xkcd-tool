package rip.deadcode.xkcdtool.core

import rip.deadcode.xkcdtool.core.OutputType.EXPLAIN
import rip.deadcode.xkcdtool.core.OutputType.IMAGE
import rip.deadcode.xkcdtool.core.OutputType.JSON
import rip.deadcode.xkcdtool.core.OutputType.NORMAL
import java.util.*
import kotlin.random.asKotlinRandom


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
        isRandom(query) ->
            when (output) {
                NORMAL -> Optional.of(Toolbox.randomUrl)
                EXPLAIN -> Optional.of(Toolbox.explainRandomUrl)
                IMAGE -> {
                    val index = askIndex()
                    getRandomId(index)
                        .flatMap { id -> askJsonFromId(id) }
                        .map { json -> json.img }
                }
                JSON -> {
                    val index = askIndex()
                    getRandomId(index).map { id -> idToJsonUrl(id) }
                }
            }
        isLatest(query) -> when (output) {
            // We just use base url for the latest comic
            NORMAL -> Optional.of(Toolbox.baseUrl)
            EXPLAIN -> Optional.of(Toolbox.explainBaseUrl)
            IMAGE -> askJson(Toolbox.latestJsonUrl).map { json -> json.img }
            JSON -> Optional.of(Toolbox.latestJsonUrl)
        }
        else -> {
            possibleId
                .flatMap { id ->
                    if (output == JSON) {
                        // // If the output is json, just convert the given id to the url
                        // TODO: JSON or JSON url?
                        Optional.of(idToJsonUrl(id))
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

fun getRandomId(index: List<IndexEntry>): OptionalInt {
    val id = index.randomOrNull(Toolbox.random.asKotlinRandom())
    return if (id == null) OptionalInt.empty() else OptionalInt.of(id.id)
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
