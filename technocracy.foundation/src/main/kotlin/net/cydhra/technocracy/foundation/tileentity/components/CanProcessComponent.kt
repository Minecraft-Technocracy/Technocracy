package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound

class CanProcessComponent : AbstractComponent() {
    override val type: ComponentType = ComponentType.OTHER

    override fun serializeNBT(): NBTTagCompound {
        TODO("not implemented")
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        TODO("not implemented")
    }

}