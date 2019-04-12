package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material

/**
 * An implementation of [IBaseBlock] that creates a tile entity with it. It inherits [AbstractRotateableBlock] so it
 * automatically has a block state with facing information. The class does not define behaviour on its own yet,
 * subclasses are required to implement tile entity interaction.
 *
 * @param unlocalizedName the unlocalized name of the block used for language lookup.
 * @param material block material
 * @param registryName the name of the block used in registries. By default the unlocalized name is used.
 * @param colorMultiplier special color multiplier for block texture. Null by default.
 */
abstract class BaseTileEntityBlock(unlocalizedName: String,
                                   registryName: String = unlocalizedName,
                                   colorMultiplier: ConstantBlockColor? = null,
                                   material: Material)
    : AbstractRotateableBlock(unlocalizedName, material, registryName, colorMultiplier), ITileEntityProvider