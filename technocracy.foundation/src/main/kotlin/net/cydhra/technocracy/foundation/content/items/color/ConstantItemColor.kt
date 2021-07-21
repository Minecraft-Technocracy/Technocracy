package net.cydhra.technocracy.foundation.content.items.color

import net.cydhra.technocracy.foundation.api.blocks.color.IBlockColor
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * An entity that returns a constant value as item color for immutably tinted items
 */
class ConstantItemColor(private val constantItemColor: Int) : IBlockColor {
    override fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int {
        return -1
    }

    override fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int {
        return this.constantItemColor
    }
}