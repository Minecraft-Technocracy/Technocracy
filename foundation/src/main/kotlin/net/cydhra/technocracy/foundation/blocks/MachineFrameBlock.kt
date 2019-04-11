package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.general.AbstractBaseBlock
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.BlockRenderLayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class MachineFrameBlock : AbstractBaseBlock("machine_frame", material = Material.ROCK) {
    init {
        this.setCreativeTab(CreativeTabs.MATERIALS)
        this.setHardness(2f)
        this.setResistance(4f)
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.TRANSLUCENT
    }

    @Suppress("OverridingDeprecatedMember") // overriding is fine
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }
}