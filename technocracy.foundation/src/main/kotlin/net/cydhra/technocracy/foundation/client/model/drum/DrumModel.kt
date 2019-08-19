package net.cydhra.technocracy.foundation.client.model.drum

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import net.cydhra.technocracy.foundation.client.model.AbstractCustomModel
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.fluids.FluidRegistry
import java.util.function.Function


class DrumModel : AbstractCustomModel() {

    companion object {
        private val cover = ResourceLocation("technocracy.foundation", "block/drum/cover")
    }

    override fun bake(state: IModelState?, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation,
            TextureAtlasSprite>): IBakedModel {

        val modelBase: IModel = ModelLoaderRegistry.getModelOrLogError(MODEL_BASE!!, "Model (${MODEL_BASE!!}) not " +
                "found has it the right name?")
        //Hand renderer
        if (state == null) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            return modelBase.bake(null, format, bakedTextureGetter)
        } else {
            val cover: IModel = ModelLoaderRegistry.getModel(cover)
            return DrumBakedModel(modelBase.bake(state, format, bakedTextureGetter), cover.bake(state, format,
                    bakedTextureGetter))
        }
    }

    override fun getTextures(): MutableCollection<ResourceLocation> {
        val tex = mutableListOf<ResourceLocation>()
        tex.addAll(super.getTextures())
        val a = ModelLoaderRegistry.getModel(cover).textures.first()
        tex.add(a)
        tex.add(FluidRegistry.WATER.still)
        return tex
    }

    override fun getDependencies(): MutableCollection<ResourceLocation> {
        return ImmutableList.builder<ResourceLocation>().add(MODEL_BASE!!).add(cover).build();
    }
}