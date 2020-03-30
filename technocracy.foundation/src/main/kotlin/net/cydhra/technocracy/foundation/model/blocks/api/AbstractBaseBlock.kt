package net.cydhra.technocracy.foundation.model.blocks.api

import net.cydhra.technocracy.foundation.model.blocks.color.IBlockColor
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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
                                 override val colorMultiplier: IBlockColor? = null,
                                 val oreDictionaryName: String? = null, renderLayer: BlockRenderLayer? = null) : Block(material), IBaseBlock {

    override val modelLocation: String
        get() = this.registryName.toString()

    override val generateItem: Boolean
        get() = true

    val renderLayers = mutableListOf<BlockRenderLayer>()

    init {
        if (renderLayer != null)
            renderLayers.add(renderLayer)

        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        return if(renderLayers.isNotEmpty()) this.renderLayers.contains(layer) else layer == blockLayer
    }

    @SideOnly(Side.CLIENT)
    /**
     * Returns the BlockRenderLayer of this block, if 2 or more layers are active it returns the first one.
     * If no layer is present it returns the super layer.
     */
    override fun getBlockLayer(): BlockRenderLayer? {
        return when {
            renderLayers.isNotEmpty() -> {
                this.renderLayers[0]
            }
            else -> super.getBlockLayer()
        }
    }
}