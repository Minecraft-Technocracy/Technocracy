package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.general.AbstractBaseLiquid
import net.cydhra.technocracy.foundation.liquids.general.oil
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess


class OilBlock : AbstractBaseLiquid(oil, "oil", Material.WATER) {

    init {
        fluid.block = this
    }

    override fun onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos) {

        val changed = world.getBlockState(neighbor)


        super.onNeighborChange(world, pos, neighbor)
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }
}