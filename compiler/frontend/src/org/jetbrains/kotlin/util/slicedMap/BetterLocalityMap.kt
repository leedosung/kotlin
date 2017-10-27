/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.util.slicedMap

import java.util.*
import java.util.function.BiConsumer

// binary representation of fractional part of phi = (sqrt(5) - 1) / 2
private const val MAGIC: Int = -1268542259 // 0xB46394CD
private const val MAX_SHIFT = 27
private const val THRESHOLD = ((1L shl 32) * 0.5).toInt() // 50% fill factor for speed
private val EMPTY_ARRAY = arrayOf<Any?>()


// For more details see for Knuth's multiplicative hash with golden ratio
// Shortly, we're trying to keep distribution of it uniform independently of input
// It's necessary because we use very simple linear probing
@Suppress("NOTHING_TO_INLINE")
private inline fun Any.computeHash(shift: Int) = ((hashCode() * MAGIC) ushr shift) shl 1

/**
 * The main ideas that might lead to better locality:
 * - Storing values in the same array as keys
 * - Using linear probes to avoid jumping to to new random indices
 *
 * This Map implementation is not intended to follow some of the maps' contracts:
 * - `put` doesn't returns previous value
 * - `entries` set is not effectively mutable
 */
class BetterLocalityMap<K : Any, V : Any> : AbstractMutableMap<K, V>() {
    // fields be initialized later in `clear()`

    // capacity = 1 << (32 - shift)
    private var shift = 0
    // keys are stored in even elements, values are in odd ones
    private var array = EMPTY_ARRAY
    // arraySize must be equal to capacity * 2, i.e. (1 << (33 - shift))
    private var arraySize: Int = 0
    private var size_ = 0

    init {
        clear()
    }

    override val size
        get() = size_

    override fun get(key: K): V? {
        var i = key.computeHash(shift)
        var k = array[i]

        while (true) {
            if (k === null) return null
            @Suppress("UNCHECKED_CAST")
            if (k == key) return array[i + 1] as V
            if (i == 0) {
                i = arraySize
            }
            i -= 2
            k = array[i]
        }
    }

    /**
     * Never returns previous values
     */
    override fun put(key: K, value: V): V? {
        if (put(array, arraySize, shift, key, value)) {
            if (++size_ >= (THRESHOLD ushr shift)) {
                rehash()
            }
        }

        return null
    }

    private fun rehash() {
        val newShift = maxOf(shift - 3, 0)
        val newArraySize = 1 shl (33 - newShift)
        val newArray = arrayOfNulls<Any>(newArraySize)

        var i = 0
        while (i < arraySize) {
            val key = array[i]
            if (key != null) {
                put(newArray, newArraySize, newShift, key, array[i + 1])
            }
            i += 2
        }

        shift = newShift
        arraySize = newArraySize
        array = newArray
    }

    override fun clear() {
        shift = MAX_SHIFT
        arraySize = 1 shl (33 - shift)
        array = arrayOfNulls(arraySize)

        size_ = 0
    }

    override fun forEach(action: BiConsumer<in K, in V>) {
        var i = 0
        while (i < arraySize) {
            val key = array[i]
            if (key != null) {
                @Suppress("UNCHECKED_CAST")
                action.accept(key as K, array[i + 1] as V)
            }
            i += 2
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            val result = HashSet<MutableMap.MutableEntry<K, V>>(size)

            forEach { k, v ->
                result.add(MyEntry(k, v))
            }

            return Collections.unmodifiableSet(result)
        }

    private class MyEntry<K, V>(
            override val key: K,
            override val value: V
    ) : MutableMap.MutableEntry<K, V> {
        override fun setValue(newValue: V): V {
            throw IllegalStateException("BetterLocalityMap.MyEntry::setValue is not supported and hardly will be")
        }
    }
}

private fun put(array: Array<Any?>, arraySize: Int, aShift: Int, key: Any, value: Any?): Boolean {
    var i = key.computeHash(aShift)
    var k = array[i]

    while (true) {
        if (k == null) {
            array[i] = key
            array[i + 1] = value
            return true
        }
        if (key == k) break
        if (i == 0) {
            i = arraySize
        }
        i -= 2
        k = array[i]
    }

    array[i + 1] = value

    return false
}
