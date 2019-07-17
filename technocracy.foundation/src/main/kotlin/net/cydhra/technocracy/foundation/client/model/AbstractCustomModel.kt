package net.cydhra.technocracy.foundation.client.model

import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel


abstract class AbstractCustomModel : IModel {
    protected var MODEL_BASE: ResourceLocation? = null

    fun initModel(modName: String, type: String, modelName: String): IModel {
        MODEL_BASE = ResourceLocation(modName, "$type/${modelName}_base")
        return this
    }
}