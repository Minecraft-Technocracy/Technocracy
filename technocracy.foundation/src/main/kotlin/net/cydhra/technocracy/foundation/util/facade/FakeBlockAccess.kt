package net.cydhra.technocracy.foundation.util.facade

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World


class FakeBlockAccess(var access: World, var fake: IBlockState, var pos: BlockPos) : IBlockAccess by access {
    override fun getBlockState(pos: BlockPos): IBlockState {
        if (pos == this.pos)
            return fake
        return access.getBlockState(pos)
    }
}