package net.cydhra.technocracy.foundation.util

import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import java.util.*

/**
 * DSL builder for an [NBTTagCompound]. It takes a block of code that can be used to easily add key-value-pairs to
 * the compound.
 * # Usage:
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
 * # Usage
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