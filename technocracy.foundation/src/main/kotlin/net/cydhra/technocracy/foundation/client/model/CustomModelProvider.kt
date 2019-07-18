package net.cydhra.technocracy.foundation.client.model

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel

class CustomModelProvider(val builtInModels: Map<String, IModel>, val modName: String) : ICustomModelLoader {
    override fun accepts(modelLocation: ResourceLocation): Boolean {
        return if (modelLocation.resourceDomain != modName) {
            false
        } else this.builtInModels.containsKey(modelLocation.resourcePath)
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel? {
        return this.builtInModels[modelLocation.resourcePath]
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        for (model in this.builtInModels.values) {
            if (model is IResourceManagerReloadListener) {
                (model as IResourceManagerReloadListener).onResourceManagerReload(resourceManager)
            }
        }
    }
}