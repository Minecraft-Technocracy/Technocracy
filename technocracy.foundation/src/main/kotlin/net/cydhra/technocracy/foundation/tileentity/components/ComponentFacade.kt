package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.items.general.facadeItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

class ComponentFacade : AbstractComponent() {
    var facades = mutableMapOf<EnumFacing, ItemStack>()

    override val type: ComponentType = ComponentType.FACADE

    override fun serializeNBT(): NBTTagCompound {
        val base = NBTTagCompound()
        for (facing in EnumFacing.values()) {
            if (facades.containsKey(facing)) {
                val stack = facades[facing]!!
                val stackNbt = stack.tagCompound!!
                base.setTag(facing.name, stackNbt)
            }
        }
        return base
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        val base = nbt as NBTTagCompound
        for (facing in EnumFacing.values()) {
            if (base.hasKey(facing.name)) {
                val stackNBT = base.getCompoundTag(facing.name)
                val stack = ItemStack(facadeItem, 1)
                stack.tagCompound = stackNBT
                facades[facing] = stack
            }
        }
    }
}