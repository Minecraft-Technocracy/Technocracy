package net.cydhra.technocracy.foundation.client.model

import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel


abstract class AbstractCustomModel : IModel {
    protected var MODEL_BASE: ResourceLocation? = null
    protected var modelName: String? = null
    protected var modName: String? = null
    protected var modeType: String? = null

    fun initModel(modName: String, type: String, modelName: String): IModel {
        this.modelName = modelName
        this.modName = modName
        this.modeType = type
        MODEL_BASE = ResourceLocation(modName, "$type/${modelName}_base")
        return this
    }
}