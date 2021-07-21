package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.api.blocks.color.IBlockColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer

/**
 * A plain block without any further behavior, except for static settings applying to all states of this block.
 *
 * @param unlocalizedName registry and language file name
 * @param material minecraft [Material] of the block
 */
open class PlainBlock(unlocalizedName: String,
                      material: Material,
                      private val opaque: Boolean = true,
                      renderLayer: BlockRenderLayer = BlockRenderLayer.SOLID,
                      colorMultiplier: IBlockColor? = null,
                      oreDictionaryName: String? = null)
    : AbstractBaseBlock(unlocalizedName, material, colorMultiplier = colorMultiplier, oreDictionaryName = oreDictionaryName, renderLayer = renderLayer) {

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return this.opaque
    }

}