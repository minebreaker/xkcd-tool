package rip.deadcode.xkcdtool.core

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

fun askUrl(query: List<String>, isExplainMode: Boolean): Optional<String> {

    val possibleId = isId(query)
    val url = when {
        isRandom(query) -> {
            if (isExplainMode) {
                Toolbox.explainRandomUrl
            } else {
                Toolbox.randomUrl
            }
        }
        isLatest(query) -> {
            // We just use base url for the latest comic
            if (isExplainMode) {
                Toolbox.explainBaseUrl
            } else {
                Toolbox.baseUrl
            }
        }
        else -> {
            if (possibleId.isPresent) {
                val id = possibleId.asInt
                val _json = askJsonFromId(id)  // Make sure the id exists
                // TODO: if 404 fallback to index search
                if (isExplainMode) {
                    idToExplainUrl(id)
                } else {
                    idToComicUrl(id)
                }
            } else {
                val index = askIndex()

                val matchedOptional = match(query, index.stream()) { e -> regularize(e.rawTitle) }
                if (matchedOptional.isEmpty) {
                    return Optional.empty()
                }
                val matched = matchedOptional.get()

                if (isExplainMode) {
                    idToExplainUrl(matched.id)
                } else {
                    idToComicUrl(matched.id)
                }
            }
        }
    }

    return Optional.ofNullable(url)
}
