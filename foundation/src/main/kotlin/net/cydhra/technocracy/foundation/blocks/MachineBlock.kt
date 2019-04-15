package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.general.BaseTileEntityBlock
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class MachineBlock(name: String, private val tileEntityConstructor: () -> TileEntity)
    : BaseTileEntityBlock(name, material = Material.ROCK) {
    init {
        this.setCreativeTab(CreativeTabs.MATERIALS)
        this.setHardness(2f)
        this.setResistance(4f)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return tileEntityConstructor()
    }

    @Suppress("OverridingDeprecatedMember")
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }
}