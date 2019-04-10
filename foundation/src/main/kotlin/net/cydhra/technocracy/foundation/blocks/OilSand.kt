package net.cydhra.technocracy.foundation.blocks

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState

class OilSand : BaseBlock("oil_sand", material = Material.SAND) {

    init {
        this.soundType = SoundType.SAND
        this.blockHardness = 1.0f
        this.blockResistance = 1.0f
    }

    override fun isToolEffective(type: String, state: IBlockState): Boolean {
        return type == "shovel"
    }
}