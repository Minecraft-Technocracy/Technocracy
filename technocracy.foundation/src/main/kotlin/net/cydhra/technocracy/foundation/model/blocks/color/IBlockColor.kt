package net.cydhra.technocracy.foundation.model.blocks.color

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess


interface IBlockColor /*: IBlockColor, IItemColor*/ {
    fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int
    fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int
}