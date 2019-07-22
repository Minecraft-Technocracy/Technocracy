package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound

class ProgressComponent: IComponent {

    override val type: ComponentType = ComponentType.PROGRESS

    /**
     * progress from 0 to 100
     */
    var progress: Int = 0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply { setInteger("progress", progress) }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        progress = nbt.getInteger("type")
    }

}