package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.general.BaseTileEntityBlock
import net.cydhra.technocracy.foundation.tileentity.TileEntityPulverizer
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World


class Pulverizer : BaseTileEntityBlock("pulverizer", material = Material.ROCK) {

    init {
        this.setCreativeTab(CreativeTabs.MATERIALS)
        this.setHardness(2f)
        this.setResistance(4f)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityPulverizer()
    }
}