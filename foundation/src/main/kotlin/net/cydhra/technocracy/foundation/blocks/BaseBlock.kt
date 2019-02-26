package net.cydhra.technocracy.foundation.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material

/**
 * Base class for all blocks added by this modification
 */
abstract class BaseBlock(unlocalizedName: String,
                         registryName: String = unlocalizedName,
                         val colorMultiplier: ConstantBlockColor? = null,
                         material: Material) : Block(material) {

    /**
     * The location where to expect the model. This is usually congruent to the registry name, except when the child
     * class does not want an extra model and texture just for this single block
     */
    open val modelLocation: String
        get() = this.registryName.toString()

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}