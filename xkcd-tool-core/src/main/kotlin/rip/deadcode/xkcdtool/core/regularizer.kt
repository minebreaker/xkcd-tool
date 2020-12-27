package rip.deadcode.xkcdtool.core

import java.text.Normalizer


/**
 * This functions regularizes input query to (which I call) the canonical xkcd name.
 * Canonicalization is done as following:
 *
 * * Change to lower cases
 * * Convert diacritics
 * * Split by spaces
 * * Remove a trailing comma from each parts
 * * Remove apostrophes // Since it's tedious to type with a keyboard
 * * Remove braces around each parts
 *     * Single `(` should be matched for #859  // Special treatment?
 * * Split by hyphens, underscores, colons, semicolons
 * * Split trailing exclamation and question marks  // Should be matchable
 *
 * Note: we don't remove non-ascii characters
 */
fun regularize(input: String): List<String> {
    return input
        .split(" ")
        .map { it.toLowerCase() }
        .map { stripAccents(it) }
        .map { if (it.endsWith(",")) it.dropLast(1) else it }
        .map { it.replace("\'", "") }
        .map { if (it.startsWith("(") && it.endsWith(")")) it.drop(1).dropLast(1) else it }
        .flatMap { it.split("-", "_", ":", ";") }
        .flatMap {
            when {
                it.endsWith("!") -> listOf(it.dropLast(1), "!")
                it.endsWith("?") -> listOf(it.dropLast(1), "?")
                else -> listOf(it)
            }
        }
}


val stripAccentsRegex = Regex("\\p{InCombiningDiacriticalMarks}+")

/**
 * [https://stackoverflow.com/q/15190656]()
 */
fun stripAccents(str: String): String {
    return Normalizer.normalize(str, Normalizer.Form.NFD)
        .replace(stripAccentsRegex, "")
}
