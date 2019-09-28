package net.cydhra.technocracy.foundation.blocks.util

import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState


interface IDynamicBlockStateContainer {
    fun addPropertyToState(state: IBlockState): IBlockState
    fun addPropertyToBuilder(builder: BlockStateContainer.Builder): BlockStateContainer.Builder
}