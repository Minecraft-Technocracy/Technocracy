package net.cydhra.technocracy.foundation.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material

/**
 * Base class for all blocks added by this modification
 */
abstract class BaseBlock(unlocalizedName: String,
                         registryName: String = unlocalizedName,
                         material: Material) : Block(material) {

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}