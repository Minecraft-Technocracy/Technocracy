package net.cydhra.technocracy.astronautics.model.items.color

import net.cydhra.technocracy.foundation.api.blocks.color.IBlockColor
import net.minecraft.block.state.IBlockState
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess


object ConcreteSprayerColor : IBlockColor {
    override fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int {
        return -1
    }

    override fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int {
        if (tintIndex == 0)
            return -1
        val color = getConcreteType(stack)
        if (color != null)
            return color.colorValue
        return -1
    }

    fun getConcreteType(stack: ItemStack): EnumDyeColor? {
        val nbt = stack.tagCompound ?: return null
        val color = nbt.getInteger("color")
        return EnumDyeColor.byMetadata(color)
    }
}