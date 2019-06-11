package net.cydhra.technocracy.foundation.client.model.facade

import net.cydhra.technocracy.foundation.client.model.pipe.FacadeBakery
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import java.util.*
import javax.vecmath.Matrix4f


class FacadeItemBakedModel(val baseBakedModel: IBakedModel, val facadeBlock: ItemStack) : IBakedModel by baseBakedModel {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        if (side != null) {
            return Collections.emptyList()
        }
        val quads = ArrayList<BakedQuad>()
        quads.addAll(FacadeBakery.getFacadeItemQuads(facadeBlock))
        quads.addAll(baseBakedModel.getQuads(state, side, rand))
        return quads
    }

    override fun handlePerspective(type: ItemCameraTransforms.TransformType): org.apache.commons.lang3.tuple.Pair<out IBakedModel, Matrix4f> {
        val pair = this.baseBakedModel.handlePerspective(type)
        return org.apache.commons.lang3.tuple.Pair.of(this, pair.value)
    }

    override fun getParticleTexture(): TextureAtlasSprite {
        return this.baseBakedModel.particleTexture
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return this.baseBakedModel.getItemCameraTransforms()
    }

    override fun isAmbientOcclusion(): Boolean {
        return this.baseBakedModel.isAmbientOcclusion
    }
}