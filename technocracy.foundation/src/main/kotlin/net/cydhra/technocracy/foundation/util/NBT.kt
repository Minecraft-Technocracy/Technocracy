package net.cydhra.technocracy.foundation.util

import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import java.util.*

/**
 * DSL builder for an [NBTTagCompound]. It takes a block of code that can be used to easily add key-value-pairs to
 * the compound.
 * # Example:
 * ```
 * compound {
 *     "key1" to 1
 *     "key2" to compound {
 *         "uuid" to UUID.randomUUID()
 *         "value" to "some string"
 *     }
 * }
 * ```
 */
fun compound(block: CompoundBuilder.() -> Unit): NBTTagCompound {
    return CompoundBuilder(NBTTagCompound()).apply(block).compound
}

/**
 * DSL function to build an [NBTTagList] from an array of values. The type of the array must be homogeneous.
 * # Example
 * ```
 * tagList(1, 2, 3)
 * ```
 *
 * This will produce an [NBTTagList] with three [NBTTagInt] entries.
 */
fun <T : Any> tagList(vararg values: T): NBTTagList {
    return values.map(::valueToTag).fold(NBTTagList()) { list, e -> list.apply { list.appendTag(e) } }
}

/**
 * DSL function to build an [NBTTagList] using a block of code. The type of tags must be homogeneous.
 * # Example
 * ```
 * constructTagList<Int> {
 *     for (i in 1..12) {
 *         appendToNBT(i*2)
 *     }
 * }
 * ```
 */
fun <T : Any> constructTagList(block: TagListBuilder<T>.() -> Unit): NBTTagList {
    return TagListBuilder<T>(NBTTagList()).apply(block).tagList
}

/**
 * DSL function to build an [NBTTagList] by automatically creating [NBTBase] tags from a collection of type [T]. If
 * the type is unsupported, an [IllegalArgumentException] will be thrown for the first element in the given [Collection]
 * # Example
 * ```
 * listOf(1, 2, 3).toNBTTagList()
 * ```
 */
fun <T : Any> Collection<T>.toNBTTagList(): NBTTagList {
    return this.map(::valueToTag).fold(NBTTagList()) { list, e -> list.apply { appendTag(e) } }
}

/**
 * DSL function to build an [NBTTagList] by automatically creating [N] tags from a collection of type [T] using a
 * [mappingFunction]. If [T] is unsupported, an [IllegalArgumentException] will be thrown for the first element
 * in the given [Collection].
 * # Example
 * ```
 * listOf(Pair("player1", 3), Pair("player2", 4)).toNBTTagList { (name, score) ->
 *     compound {
 *         "name" to name
 *         "score" to score
 *     }
 * }
 * ```
 */
fun <T : Any, N : NBTBase> Collection<T>.toNBTTagList(mappingFunction: (T) -> N): NBTTagList {
    return this.map(mappingFunction).fold(NBTTagList()) { list, e -> list.apply { appendTag(e) } }
}

/**
 * A helper class that provides a context callsite for [net.cydhra.technocracy.foundation.util.compound].
 */
class CompoundBuilder(val compound: NBTTagCompound) {

    /**
     * Add a key-value-pair to the current [NBTTagCompound]. The value can be anything that can be represented as an
     * [NBTBase] tag. If an unsupported type is given, an [IllegalArgumentException] will be thrown
     */
    infix fun String.to(value: Any) {
        compound.setTag(this, valueToTag(value))
    }
}

class TagListBuilder<T : Any>(val tagList: NBTTagList) {

    /**
     * Append a value of type [T] to the current tag list. If [T] is unsupported by NBT, an
     * [IllegalArgumentException] will be thrown
     */
    fun appendToNBT(value: T) {
        tagList.appendTag(valueToTag(value))
    }
}

/**
 * Construct an [NBTBase] tag from any value. If an unsupported type is given, an [IllegalArgumentException] will be
 * thrown
 */
fun valueToTag(value: Any): NBTBase = when (value) {
    is Boolean -> NBTTagByte(if (value) 1 else 0)
    is Byte -> NBTTagByte(value)
    is Short -> NBTTagShort(value)
    is Int -> NBTTagInt(value)
    is Long -> NBTTagLong(value)
    is Float -> NBTTagFloat(value)
    is Double -> NBTTagDouble(value)
    is String -> NBTTagString(value)
    is ByteArray -> NBTTagByteArray(value)
    is IntArray -> NBTTagIntArray(value)
    is UUID -> NBTUtil.createUUIDTag(value)
    is BlockPos -> NBTUtil.createPosTag(value)
    is Enum<*> -> NBTTagInt(value.ordinal)
    is NBTBase -> value
    else -> throw IllegalArgumentException("type unsupported")
}