package net.cydhra.technocracy.foundation.model.blocks.color

import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess


object DyeBlockColor : IBlockColor {
    val COLOR = PropertyEnum.create("color", EnumDyeColor::class.java)

     override fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int {
        return (state.properties[COLOR] as EnumDyeColor).colorValue
    }

     override fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int {
        return EnumDyeColor.values()[stack.metadata].colorValue
    }
}