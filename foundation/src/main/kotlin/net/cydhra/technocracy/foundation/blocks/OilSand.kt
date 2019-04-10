package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.general.AbstractFallingBaseBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs

class OilSand : AbstractFallingBaseBlock("oil_sand", material = Material.SAND) {

    init {
        this.soundType = SoundType.SAND
        this.blockHardness = 0.5f
        this.blockResistance = 0.5f

        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS)
    }

    override fun isToolEffective(type: String, state: IBlockState): Boolean {
        return type == "shovel"
    }
}