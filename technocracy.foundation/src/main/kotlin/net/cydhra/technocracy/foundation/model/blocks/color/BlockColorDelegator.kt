package net.cydhra.technocracy.foundation.model.blocks.color

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess


class BlockColorDelegator(val blockColor: IBlockColor) : net.minecraft.client.renderer.color.IBlockColor, IItemColor {
    override fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int {
        return blockColor.colorMultiplier(state, worldIn, pos, tintIndex)
    }

    override fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int {
        return blockColor.colorMultiplier(stack, tintIndex)
    }
}