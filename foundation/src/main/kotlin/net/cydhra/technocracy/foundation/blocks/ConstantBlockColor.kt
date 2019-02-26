package net.cydhra.technocracy.foundation.blocks

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Implementation of [IBlockColor] that does return the same color in any case. Used for tinting blocks independent
 * of state and location
 */
class ConstantBlockColor(private val constantItemColor: Int) : IBlockColor {
    override fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int {
        return if (tintIndex == 1)
            this.constantItemColor
        else
            -1
    }
}