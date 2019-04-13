package net.cydhra.technocracy.foundation.client.model

import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel


abstract class AbstractCustomModel : IModel {
    protected var MODEL_BASE: ResourceLocation = ResourceLocation("technocracy.foundation",
            "block/electric_furnace_base")

    fun initModel(modelName: String): IModel {
        MODEL_BASE = ResourceLocation("technocracy.foundation", "block/${modelName}_base")
        return this
    }
}