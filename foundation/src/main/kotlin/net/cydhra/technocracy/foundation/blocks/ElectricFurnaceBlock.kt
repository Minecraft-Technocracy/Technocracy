package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.general.BaseTileEntityBlock
import net.cydhra.technocracy.foundation.tileentity.TileEntityElectricFurnace
import net.cydhra.technocracy.foundation.tileentity.TileEntityPulverizer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.world.World


class ElectricFurnaceBlock : BaseTileEntityBlock("electric_furnace", material = Material.ROCK) {

    init {
        this.setCreativeTab(CreativeTabs.MATERIALS)
        this.setHardness(2f)
        this.setResistance(4f)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityElectricFurnace()
    }

    @Suppress("OverridingDeprecatedMember")
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

}