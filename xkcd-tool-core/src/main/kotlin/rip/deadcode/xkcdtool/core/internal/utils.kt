package rip.deadcode.xkcdtool.core.internal

import com.google.common.cache.Cache
import java.util.*
import kotlin.reflect.KClass


fun Any?.anyToInt(): Optional<Int> {
    return if (this == null) {
        Optional.empty()
    } else {
        Optional.ofNullable(this.toString().toIntOrNull())
    }
}

fun <T : Any> Any?.cast(cls: KClass<T>): Optional<T> {
    return if (cls.isInstance(this)) {
        @Suppress("UNCHECKED_CAST")
        Optional.of(this as T)
    } else {
        Optional.empty()
    }
}

fun <T> T?.optional(): Optional<T> = Optional.ofNullable(this)

fun <K, V> Cache<K, V>.get(key: K): Optional<V> {
    return Optional.ofNullable(this.getIfPresent(key))
}
