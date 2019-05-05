package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.cydhra.technocracy.foundation.client.technocracyCreativeTabs
import net.minecraft.block.BlockFalling
import net.minecraft.block.material.Material

/**
 * An implementation of [IBaseBlock] that inherits [BlockFalling] and therefore is used as a base class for all
 * blocks affected by gravity.
 *
 * @param unlocalizedName the unlocalized name of the block used for language lookup.
 * @param material block material
 * @param registryName the name of the block used in registries. By default the unlocalized name is used.
 * @param colorMultiplier special color multiplier for block texture. Null by default.
 */
abstract class AbstractFallingBaseBlock(unlocalizedName: String,
                                        material: Material,
                                        registryName: String = unlocalizedName,
                                        override val colorMultiplier: ConstantBlockColor? = null) : BlockFalling(material), IBaseBlock {

    override val modelLocation: String
        get() = this.registryName.toString()

    override val generateItem: Boolean
        get() = true

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
        this.setCreativeTab(technocracyCreativeTabs)
    }
}