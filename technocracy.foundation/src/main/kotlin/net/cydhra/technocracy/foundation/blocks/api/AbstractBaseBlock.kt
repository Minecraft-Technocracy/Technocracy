package net.cydhra.technocracy.foundation.blocks.api

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.cydhra.technocracy.foundation.blocks.color.IBlockColor
import net.cydhra.technocracy.foundation.client.technocracyCreativeTabs
import net.minecraft.block.Block
import net.minecraft.block.material.Material

/**
 * Base class for simple blocks with no further requirements. It is a naive implementation of [IBaseBlock] and
 * directly inherits [Block].
 *
 * @param unlocalizedName the unlocalized name of the block used for language lookup.
 * @param material block material
 * @param registryName the name of the block used in registries. By default the unlocalized name is used.
 * @param colorMultiplier special color multiplier for block texture. Null by default.
 */
abstract class AbstractBaseBlock(unlocalizedName: String,
                                 material: Material,
                                 registryName: String = unlocalizedName,
                                 override val colorMultiplier: IBlockColor? = null) : Block(material), IBaseBlock {

    override val modelLocation: String
        get() = this.registryName.toString()

    override val generateItem: Boolean
        get() = true

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}