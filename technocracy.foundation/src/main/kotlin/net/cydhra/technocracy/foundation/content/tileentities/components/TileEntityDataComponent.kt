package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.api.ecs.tileentities.AbstractTileEntityComponent
import net.cydhra.technocracy.foundation.util.valueToTag
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import java.lang.reflect.Array
import java.util.*


class TileEntityDataComponent<T : Any>(default: T) : AbstractTileEntityComponent() {

    override val type = ComponentType.OTHER
    private var wrapped = DataWrapper(default, this)
    var needsRerender = false

    init {
        wrapped.setParent(this)
    }

    fun setValue(newValue: T) {
        wrapped.value = newValue
        markDirty()
    }

    fun getValue(): T {
        return wrapped.value
    }

    override fun serializeNBT(): NBTTagCompound {
        return wrapped.serialize()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        wrapped = wrapped.deserialize(nbt)
    }

    fun markDirty() {
        markDirty(this.needsRerender)
    }

    interface IDataType<T : IDataType<T>> {
        fun serialize(): NBTTagCompound
        fun deserialize(nbt: NBTTagCompound): T
        fun setParent(parent: TileEntityDataComponent<*>)
    }

    private class DataWrapper<T : Any>(value: T, var internalParent: TileEntityDataComponent<*>) : IDataType<DataWrapper<T>> {
        var value: T = value
            set(value) {
                if (value is IDataType<*>) {
                    value.setParent(internalParent)
                }
                field = value
            }

        override fun serialize(): NBTTagCompound {
            val value = value
            if (value is IDataType<*>)
                return value.serialize()

            val tag = NBTTagCompound()
            tag.setTag("value", valueToTag(value))
            return tag
        }

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(nbt: NBTTagCompound): DataWrapper<T> {
            val tmp = value

            if (tmp is IDataType<*>) {
                return DataWrapper(tmp.deserialize(nbt) as T, this.internalParent)
            }

            val value = when (val tag = nbt.getTag("value")) {
                is NBTTagByte -> {
                    when (this.value) {
                        is Byte -> tag.byte
                        is Boolean -> (tag.byte == 1.toByte())
                        else -> throw IllegalArgumentException("tag type does not match generic type T")
                    }
                }
                is NBTTagShort -> tag.short
                is NBTTagInt -> {
                    if (value is Int) {
                        tag.int
                    } else { // if T is an enum
                        Array.get(value::class.java.getMethod("values").invoke(null), tag.int)
                    }
                }
                is NBTTagLong -> tag.long
                is NBTTagFloat -> tag.float
                is NBTTagDouble -> tag.double
                is NBTTagString -> tag.string
                is NBTTagByteArray -> tag.byteArray
                is NBTTagIntArray -> tag.intArray
                is NBTTagCompound -> {
                    when (value) {
                        is BlockPos -> NBTUtil.getPosFromTag(tag)
                        is UUID -> NBTUtil.getUUIDFromTag(tag)
                        else -> throw IllegalArgumentException("cannot automatically deserialize a compound tag into type ${value::class}")
                    }
                }
                else -> throw AssertionError("tag type unsupported")
            }

            return DataWrapper(value as T, internalParent)
        }

        override fun setParent(parent: TileEntityDataComponent<*>) {
            this.internalParent = parent

            val value = value
            if (value is IDataType<*>) {
                value.setParent(parent)
            }
        }
    }
}