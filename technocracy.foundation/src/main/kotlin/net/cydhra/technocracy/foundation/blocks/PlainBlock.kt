package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.api.AbstractBaseBlock
import net.cydhra.technocracy.foundation.blocks.color.IBlockColor
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
                      private val renderLayer: BlockRenderLayer = BlockRenderLayer.SOLID,
                      colorMultiplier: IBlockColor? = null)
    : AbstractBaseBlock(unlocalizedName, material, colorMultiplier = colorMultiplier) {

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return this.opaque
    }

    override fun getBlockLayer(): BlockRenderLayer {
        return this.renderLayer
    }
}