package net.cydhra.technocracy.foundation.util.model

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BakedQuadRetextured
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.SimpleBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


@SideOnly(Side.CLIENT)
class ModelTextureRemapper(val state: IBlockState?, val model: IBakedModel, pos: BlockPos, val textureRemapper: ((sprite: TextureAtlasSprite) -> TextureAtlasSprite)) {
    private val builderGeneralQuads: MutableList<BakedQuad>
    private val builderFaceQuads: MutableMap<EnumFacing, MutableList<BakedQuad>>

    init {
        builderGeneralQuads = Lists.newArrayList()
        builderFaceQuads = Maps.newEnumMap(EnumFacing::class.java)
        for (face in EnumFacing.values()) {
            builderFaceQuads[face] = mutableListOf()
        }

        val i = MathHelper.getPositionRandom(pos)

        for (face in EnumFacing.values()) {
            addFaceQuads(state, model, face, i)
        }

        addGeneralQuads(state, model, i)
    }

    private fun addFaceQuads(state: IBlockState?, model: IBakedModel, facing: EnumFacing, rand: Long) {
        for (quad in model.getQuads(state, facing, rand)) {
            addFaceQuad(facing, BakedQuadRetextured(quad, textureRemapper.invoke(quad.sprite)))
        }
    }

    private fun addGeneralQuads(state: IBlockState?, model: IBakedModel, rand: Long) {
        for (quad in model.getQuads(state, null, rand)) {
            addGeneralQuad(BakedQuadRetextured(quad, textureRemapper.invoke(quad.sprite)))
        }
    }

    fun addFaceQuad(facing: EnumFacing, quad: BakedQuad): ModelTextureRemapper {
        builderFaceQuads[facing]?.add(quad)
        return this
    }

    fun addGeneralQuad(quad: BakedQuad): ModelTextureRemapper {
        builderGeneralQuads.add(quad)
        return this
    }

    fun makeBakedModel(): IBakedModel {
        return SimpleBakedModel(builderGeneralQuads, builderFaceQuads, model.isAmbientOcclusion(state), model.isGui3d, textureRemapper.invoke(model.particleTexture), model.itemCameraTransforms, model.overrides)
    }
}