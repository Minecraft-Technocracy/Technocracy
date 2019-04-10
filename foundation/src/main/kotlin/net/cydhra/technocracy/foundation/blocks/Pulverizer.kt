package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.general.BaseTileEntityBlock
import net.cydhra.technocracy.foundation.tileentity.TileEntityPulveriser
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World


class Pulverizer : BaseTileEntityBlock("pulverizer", material = Material.IRON) {

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityPulveriser()
    }
}