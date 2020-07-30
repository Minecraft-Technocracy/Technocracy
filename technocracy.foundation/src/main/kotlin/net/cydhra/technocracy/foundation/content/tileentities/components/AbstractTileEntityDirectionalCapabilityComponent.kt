package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.util.append
import net.cydhra.technocracy.foundation.util.tagList
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagInt
import net.minecraft.util.EnumFacing

/**
 * Defines structure for capabilities with a facing that also implement a capability.
 */
abstract class AbstractTileEntityDirectionalCapabilityComponent : AbstractTileEntityCapabilityComponent() {
    abstract val facing: MutableSet<EnumFacing>

    abstract fun getDirection(): Direction

    abstract fun deserialize(nbt: NBTTagCompound)
    abstract fun serialize(): NBTTagCompound

    override fun serializeNBT(): NBTTagCompound {
        val tag = serialize()

        synchronized(facing) {
            append(tag) {
                "facings" to tagList(*facing.toSet().toTypedArray())
            }
        }

        return tag
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        deserialize(nbt)

        synchronized(facing) {
            if (nbt.hasKey("facings")) {
                facing.clear()
                val list = nbt.getTagList("facings", 3)
                for (i in list.iterator()) {
                    facing.add(EnumFacing.values()[(i as NBTTagInt).int])
                }
            }
        }
    }

    enum class Direction {
        INPUT, OUTPUT, BOTH, NONE
    }
}