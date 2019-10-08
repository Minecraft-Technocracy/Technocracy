package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound

class ProgressComponent: AbstractComponent() {

    override val type: ComponentType = ComponentType.OTHER

    /**
     * progress from 0 to 100
     */
    var progress: Int = 0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply { setInteger("progress", progress) }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        progress = nbt.getInteger("progress")
    }

}