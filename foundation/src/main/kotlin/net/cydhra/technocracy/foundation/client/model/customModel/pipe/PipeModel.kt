package net.cydhra.technocracy.foundation.client.model.customModel.pipe

import com.google.common.collect.ImmutableList
import net.cydhra.technocracy.foundation.client.model.AbstractCustomModel
import net.cydhra.technocracy.foundation.client.model.customModel.connector.MachineConnectorBakedModel
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.model.IModelState
import java.util.function.Function


class PipeModel : AbstractCustomModel() {

    init {
        println("LOADEDsadadsffdsfs")
    }

    private val one_normal = ResourceLocation("technocracy.foundation", "block/extra/pipe/single_normal")
    private val two_normal = ResourceLocation("technocracy.foundation", "block/extra/pipe/double_normal")
    private val three_normal = ResourceLocation("technocracy.foundation", "block/extra/pipe/triple_normal")
    private val four_normal = ResourceLocation("technocracy.foundation", "block/extra/pipe/quad_normal")

    override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {

        println("ddddddddddddddddddddddd")

        val modelBase: IModel = ModelLoaderRegistry.getModelOrLogError(MODEL_BASE!!, "Model (${MODEL_BASE!!}) not " +
                "found has it the right name?")

        println(state)

        //Hand renderer
        if (state == null) {
            return ModelLoaderRegistry.getModel(one_normal).bake(state, format, bakedTextureGetter)
        } else {
            val models = mutableMapOf<String, IBakedModel>()

            val keywords = arrayListOf("single", "double", "triple", "quad")

            for (str in keywords) {
                models[str + "_normal"] = ModelLoaderRegistry.getModel(getResourceLocation(str + "_normal")).bake(state, format,
                        bakedTextureGetter)
            }

            for (facing in EnumFacing.values()) {
                for (str in keywords) {
                    models[str + "_extended_" + facing.name] =
                            ModelLoaderRegistry.getModelOrMissing(getResourceLocation(str + "_extended_" + facing.name))
                                    .bake(state, format, bakedTextureGetter)
                }
            }

            return PipeBakedModel(ModelLoaderRegistry.getModel(one_normal).bake(state, format, bakedTextureGetter), models)
        }
    }

    fun getResourceLocation(name: String): ResourceLocation {
        return ResourceLocation("technocracy.foundation", "block/extra/pipe/$name")
    }

    override fun getDependencies(): MutableCollection<ResourceLocation> {
        return ImmutableList.builder<ResourceLocation>().add(MODEL_BASE!!).addAll(listOf(one_normal, two_normal,
                three_normal, four_normal)).build()
    }
}