package net.cydhra.technocracy.foundation.client.model.wrapper

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.content.blocks.blockWrapper
import net.cydhra.technocracy.foundation.content.blocks.tileWrapper
import net.cydhra.technocracy.foundation.util.facade.FakeBlockAccess
import net.cydhra.technocracy.foundation.util.model.SimpleQuad
import net.cydhra.technocracy.foundation.util.model.pipeline.QuadPipeline
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.QuadTinter
import net.cydhra.technocracy.foundation.util.model.pipeline.consumer.clone.QuadCloneConsumer
import net.cydhra.technocracy.foundation.util.propertys.BLOCKSTATE
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.property.IExtendedBlockState


class WrappedBlockModelBakery : IBakedModel {

    override fun getQuads(stateIn: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        val pos = (stateIn as IExtendedBlockState).getValue(POSITION)
        val state = stateIn.getValue(
            BLOCKSTATE
        ) ?: return mutableListOf()

        if (!state.block.canRenderInLayer(state, MinecraftForgeClient.getRenderLayer()))
            return mutableListOf()

        if (state.block == blockWrapper || state.block == tileWrapper)
            return mutableListOf()

        val mc = Minecraft.getMinecraft()
        val fakeWorld = FakeBlockAccess(mc.world, state, pos)

        //get the custom state with ctm data
        val customState = state.getActualState(fakeWorld, pos)
        val coverModel = mc.blockRendererDispatcher.getModelForState(customState)
        val extendedState = customState.block.getExtendedState(customState, fakeWorld, pos)

        val originalQuads = coverModel.getQuads(extendedState, side, rand)

        val quads = mutableListOf<BakedQuad>()

        val tinter = QuadTinter()
        val pipeline = QuadPipeline().addConsumer(QuadCloneConsumer(true), tinter)

        originalQuads.forEachIndexed { _, bakedQuad ->
            val quad = SimpleQuad(DefaultVertexFormats.BLOCK)
            tinter.tint =
                Minecraft.getMinecraft().blockColors.colorMultiplier(customState, fakeWorld, pos, bakedQuad.tintIndex)
            quads.add(pipeline.pipe(quad, bakedQuad).bake())
        }

        return quads
    }

    override fun isAmbientOcclusion(): Boolean {
        return true
    }

    override fun isGui3d(): Boolean {
        return true
    }

    override fun isBuiltInRenderer(): Boolean {
        return true
    }

    override fun getParticleTexture(): TextureAtlasSprite {
        return TextureAtlasManager.pipe_node
    }

    override fun getOverrides(): ItemOverrideList {
        return ItemOverrideList.NONE
    }
}