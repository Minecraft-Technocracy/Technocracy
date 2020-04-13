package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.api.ecs.ComponentType
import net.cydhra.technocracy.foundation.api.ecs.tileentities.AbstractTileEntityComponent
import net.cydhra.technocracy.foundation.content.items.facadeItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

class TileEntityFacadeComponent : AbstractTileEntityComponent() {
    var facades = mutableMapOf<EnumFacing, ItemStack>()

    override val type: ComponentType = ComponentType.OTHER

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
        for (facing in EnumFacing.values()) {
            if (nbt.hasKey(facing.name)) {
                val stackNBT = nbt.getCompoundTag(facing.name)
                val stack = ItemStack(facadeItem, 1)
                stack.tagCompound = stackNBT
                facades[facing] = stack
            }
        }
    }
}