package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.cydhra.technocracy.foundation.util.compound
import net.minecraft.nbt.NBTTagCompound


class BooleanDataTileEntityComponent : AbstractTileEntityComponent() {

    var state: Boolean = false
        private set

    fun setState(newState: Boolean) {
        state = newState
        markDirty(false)
    }

    override fun serializeNBT(): NBTTagCompound {
        return compound {
            "state" to state
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (nbt.hasKey("state"))
            state = nbt.getBoolean("state")
    }

    override val type: ComponentType = ComponentType.OTHER
}