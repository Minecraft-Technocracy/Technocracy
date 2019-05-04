package net.cydhra.technocracy.foundation.blocks.general

import net.minecraft.block.material.Material

/**
 * A plain block without any further behavior
 *
 * @param unlocalizedName registry and language file name
 * @param material minecraft [Material] of the block
 */
class PlainBlock(unlocalizedName: String, material: Material) : AbstractBaseBlock(unlocalizedName, material)