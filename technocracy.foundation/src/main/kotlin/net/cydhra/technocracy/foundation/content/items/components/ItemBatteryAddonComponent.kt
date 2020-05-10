package net.cydhra.technocracy.foundation.content.items.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.minecraft.nbt.NBTTagCompound


class ItemBatteryAddonComponent(val extractionScaler: Float = 1f, val insertScaler: Float = 1f) : AbstractItemComponent() {
    override val type = ComponentType.ENERGY
    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
    }
}