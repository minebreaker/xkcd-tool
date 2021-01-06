package rip.deadcode.xkcdtool.core.internal

import com.google.common.cache.Cache
import java.util.*


fun <T> T?.optional(): Optional<T> = Optional.ofNullable(this)

fun <K, V> Cache<K, V>.get(key: K): Optional<V> {
    return Optional.ofNullable(this.getIfPresent(key))
}
