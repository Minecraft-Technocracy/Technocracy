package net.cydhra.technocracy.foundation.client.model.customModel.connector

import com.google.common.collect.ImmutableList
import net.cydhra.technocracy.foundation.client.model.AbstractCustomModel
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.model.IModelState
import java.util.function.Function


class MachineConnectorModel : AbstractCustomModel() {

    private val CONNECTORS = ResourceLocation("technocracy.foundation", "extra/connector")

    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        val modelBase: IModel = ModelLoaderRegistry.getModelOrLogError(MODEL_BASE!!, "Model (${MODEL_BASE!!}) not " +
                "found has it the right name?")
        //Hand renderer
        if (state == null) {
            return modelBase.bake(state, format, bakedTextureGetter)
        } else {
            val connectors: IModel = ModelLoaderRegistry.getModel(CONNECTORS)
            return MachineConnectorBakedModel(modelBase.bake(state, format, bakedTextureGetter), connectors.bake(state, format,
                    bakedTextureGetter))
        }
    }

    override fun getDependencies(): MutableCollection<ResourceLocation> {
        return ImmutableList.builder<ResourceLocation>().add(MODEL_BASE!!).add(CONNECTORS).build();
    }


}