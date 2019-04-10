package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.minecraft.block.Block
import net.minecraft.block.material.Material

/**
 * Base class for all blocks added by this modification
 */
abstract class AbstractBaseBlock(unlocalizedName: String,
                                 material: Material,
                                 registryName: String = unlocalizedName,
                                 override val colorMultiplier: ConstantBlockColor? = null) : Block(material), IBaseBlock {

    /**
     * The location where to expect the model. This is usually congruent to the registry name, except when the child
     * class does not want an extra model and texture just for this single block
     */
    override val modelLocation: String
        get() = this.registryName.toString()

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}