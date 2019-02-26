package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs

/**
 * Class for all ore type blocks of this modification
 */
class OreBlock(unlocalizedName: String) : BaseBlock("ore.$unlocalizedName", material = Material.ROCK) {

    override val modelLocation: String = "${TCFoundation.MODID}:ore"

    init {
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS)
    }
}