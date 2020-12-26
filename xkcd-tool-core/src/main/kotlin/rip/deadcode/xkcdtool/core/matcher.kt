package rip.deadcode.xkcdtool.core

import java.util.*
import java.util.stream.Stream


data class Score<E>(
    val score: Int,
    val entry: E
)

/**
 * Tries to match given regularized name with candidate lists
 * with following algorithm.
 * This is just a temporal hack and needs improvements.
 *
 * * Exact match
 * * Number of match counts
 * * If the number is same, first wins
 * * No match - Find likely one (TBD)
 *     Contains keywords
 *     Typo tolerant
 */
fun <E> match(query: List<String>, candidates: Stream<E>, entryToName: (E) -> List<String>): Optional<E> {
    return candidates
        .flatMap {
            val candidateName = entryToName(it)
            if (candidateName == query) {
                Stream.of(Score(Integer.MAX_VALUE, it))
            } else {
                val matchCount = query.count { q -> candidateName.contains(q) }
                if (matchCount != 0) {
                    Stream.of(Score(matchCount, it))
                } else {
                    Stream.empty()
                }
            }
        }
        .max(compareBy { it.score })
        .map { it.entry }
}
