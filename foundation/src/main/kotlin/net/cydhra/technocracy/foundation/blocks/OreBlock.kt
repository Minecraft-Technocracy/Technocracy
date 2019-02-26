package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.BlockRenderLayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


/**
 * Class for all ore type blocks of this modification
 */
class OreBlock(unlocalizedName: String) : BaseBlock("ore.$unlocalizedName", material = Material.ROCK) {

    override val modelLocation: String = "${TCFoundation.MODID}:ore"

    init {
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS)
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }
}