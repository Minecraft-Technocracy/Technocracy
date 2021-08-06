package net.cydhra.technocracy.foundation.api.blocks.util

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation


interface IBlockStateMapper {
    fun getModelResourceLocation(state: IBlockState): ModelResourceLocation
}