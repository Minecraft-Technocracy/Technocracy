package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.minecraft.nbt.NBTTagCompound

/**
 * A machine component that handles all machine upgrades.
 */
class MachineUpgradesComponents : AbstractComponent() {

    companion object {
        private const val NBT_KEY_UPGRADES = "upgrades"
    }

    override val type: ComponentType = ComponentType.OTHER

    override fun serializeNBT(): NBTTagCompound {
        TODO("not implemented")
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        TODO("not implemented")
    }
}