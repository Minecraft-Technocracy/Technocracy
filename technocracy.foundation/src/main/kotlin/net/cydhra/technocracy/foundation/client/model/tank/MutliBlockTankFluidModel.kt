package net.cydhra.technocracy.foundation.client.model.tank

import net.cydhra.technocracy.foundation.client.model.AbstractCustomModel
import net.cydhra.technocracy.foundation.client.model.customModel.connector.MachineConnectorBakedModel
import net.cydhra.technocracy.foundation.client.model.customModel.connector.MachineConnectorModel
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.model.IModelState
import java.util.function.Function


class MutliBlockFluidModel : AbstractCustomModel() {
    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
        state.m
        val modelBase: IModel = ModelLoaderRegistry.getModelOrLogError(MODEL_BASE!!, "Model (${MODEL_BASE!!}) not " +
                "found has it the right name?")

        return modelBase.bake(state, format, bakedTextureGetter)
    }
}