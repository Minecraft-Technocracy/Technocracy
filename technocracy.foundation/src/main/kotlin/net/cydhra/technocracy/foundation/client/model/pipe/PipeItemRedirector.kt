package net.cydhra.technocracy.foundation.client.model.pipe

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.items.general.PipeItem
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*


class PipeItemRedirector(val baseBakedModel: IBakedModel) : IBakedModel by baseBakedModel {
    companion object {

        private val cache = mutableMapOf<Int, IBakedModel>()
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        return Collections.emptyList()
    }

    override fun getOverrides(): ItemOverrideList {
        return object : ItemOverrideList(emptyList<ItemOverride>()) {
            override fun handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World?, entity: EntityLivingBase?): IBakedModel {
                if (stack.item !is PipeItem)
                    return originalModel

                return cache.getOrPut(stack.metadata) {
                    println("added new")
                    getModelWithTexture(null, baseBakedModel, TextureAtlasManager.getTextureForConnectionType(PipeType.values()[stack.metadata]))
                }
            }
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun getModelWithTexture(state: IBlockState?, model: IBakedModel, texture: TextureAtlasSprite): IBakedModel {
        return SimpleBakedModel.Builder(state, model, texture, BlockPos.ORIGIN).makeBakedModel()
    }
}