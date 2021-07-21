package net.cydhra.technocracy.foundation.client.model.pipe

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.cydhra.technocracy.foundation.content.items.PipeItem
import net.cydhra.technocracy.foundation.util.model.ModelTextureRemapper
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverride
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*


class PipeItemRedirector(val baseBakedModel: IBakedModel) : IBakedModel by baseBakedModel {
    companion object {
        val modelCache = mutableMapOf<Int, IBakedModel>()
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        return Collections.emptyList()
    }

    override fun getOverrides(): ItemOverrideList {
        return object : ItemOverrideList(emptyList<ItemOverride>()) {
            override fun handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World?, entity: EntityLivingBase?): IBakedModel {
                if (stack.item !is PipeItem)
                    return originalModel

                return modelCache.getOrPut(stack.metadata) {
                    getModelWithTexture(null, baseBakedModel) { sprite ->
                        if (sprite.iconName.contains("replace_me")) {
                            PipeType[stack.metadata].texture
                        } else {
                            sprite
                        }
                    }
                }
            }
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun getModelWithTexture(state: IBlockState?, model: IBakedModel, textureRemapper: ((sprite: TextureAtlasSprite) -> TextureAtlasSprite)): IBakedModel {
        return ModelTextureRemapper(state, model, BlockPos.ORIGIN, textureRemapper).makeBakedModel()
    }
}