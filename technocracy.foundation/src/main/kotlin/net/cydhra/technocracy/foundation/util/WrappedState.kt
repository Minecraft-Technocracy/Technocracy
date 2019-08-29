package net.cydhra.technocracy.foundation.util

import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class WrappedState(val state: IBlockState, val minX: Int, val minY: Int, val minZ: Int, val maxX: Int, val maxY: Int, val maxZ: Int) : IBlockState by state {

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockAccess: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val p = pos.offset(facing)//.add(-origPos.x, -origPos.y, -origPos.z)

        if (p.x < minX || p.x > maxX) return true
        if (p.y < minY || p.y > maxY) return true
        if (p.z < minZ || p.z > maxZ) return true
        return state.shouldSideBeRendered(blockAccess, pos, facing)
    }
}