package net.cydhra.technocracy.foundation.blocks.util

import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.IExtendedBlockState


interface IDynamicBlockStateContainer {
    fun addPropertyToState(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): IBlockState
    fun addExtendedPropertyToState(state: IExtendedBlockState, world: IBlockAccess?, pos: BlockPos?): IExtendedBlockState
    fun addPropertyToBuilder(builder: BlockStateContainer.Builder): BlockStateContainer.Builder
}