package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.api.AbstractBaseBlock
import net.minecraft.block.material.Material

/**
 * A plain block without any further behavior
 *
 * @param unlocalizedName registry and language file name
 * @param material minecraft [Material] of the block
 */
class PlainBlock(unlocalizedName: String, material: Material) : AbstractBaseBlock(unlocalizedName, material)