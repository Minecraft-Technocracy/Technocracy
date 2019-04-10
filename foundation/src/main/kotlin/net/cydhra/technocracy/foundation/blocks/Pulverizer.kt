package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.general.AbstractBaseBlock
import net.cydhra.technocracy.foundation.tileentity.TileEntityPulveriser
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World


class Pulverizer : AbstractBaseBlock("pulverizer", material = Material.IRON), ITileEntityProvider {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityPulveriser()
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TileEntityPulveriser()
    }
}