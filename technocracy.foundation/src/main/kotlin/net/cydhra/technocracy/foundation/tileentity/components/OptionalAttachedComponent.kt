package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound


class OptionalAttachedComponent<T : AbstractComponent>(val innerComponent: T) : AbstractComponent() {

    var isAttached = false
    override val type: ComponentType = ComponentType.OPTIONAL

    override fun serializeNBT(): NBTTagCompound {
        val comp = NBTTagCompound()
        comp.setBoolean("isAttached", isAttached)
        if (isAttached)
            comp.setTag("inner", innerComponent.serializeNBT())
        return comp
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (nbt.hasKey("isAttached") && nbt.getBoolean("isAttached")) {
            isAttached = true
            innerComponent.deserializeNBT(nbt.getCompoundTag("inner"))
        }
    }

    override fun onRegister() {
        innerComponent.tile = this.tile
    }
}