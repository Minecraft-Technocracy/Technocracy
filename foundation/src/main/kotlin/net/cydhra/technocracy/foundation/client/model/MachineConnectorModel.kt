package net.cydhra.technocracy.foundation.client.model

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.model.IModelState
import java.util.function.Function
import com.google.common.collect.ImmutableList






class MachineConnectorModel : IModel {
    private val MODEL_BASE = ResourceLocation("technocracy.foundation", "block/electric_furnace_base")

    private val extra = ResourceLocation("technocracy.foundation", "block/connector")

    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        val modelBase: IModel = ModelLoaderRegistry.getModel(MODEL_BASE)
        //Hand renderer
        if(state == null) {
            return modelBase.bake(state, format, bakedTextureGetter)
        } else {
            val connectors: IModel = ModelLoaderRegistry.getModel(extra)
            return MachineConnectorBakedModel(modelBase.bake(state, format, bakedTextureGetter), connectors.bake(state, format,
                    bakedTextureGetter))
        }
    }

    override fun getDependencies(): MutableCollection<ResourceLocation> {
        return ImmutableList.builder<ResourceLocation>().add(MODEL_BASE).add(extra).build();
    }
}