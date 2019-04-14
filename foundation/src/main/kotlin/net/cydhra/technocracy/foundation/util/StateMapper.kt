package net.cydhra.technocracy.foundation.util

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * Custom state mapper that assignes an alternative model resource location to block models
 */
class StateMapper(fileName: String, modelName: String) : StateMapperBase(), ItemMeshDefinition {

    val modelLocation = ModelResourceLocation(ResourceLocation("technocracy.foundation",
            fileName), modelName)

    override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
        return modelLocation
    }

    override fun getModelLocation(stack: ItemStack): ModelResourceLocation {
        return modelLocation
    }
}