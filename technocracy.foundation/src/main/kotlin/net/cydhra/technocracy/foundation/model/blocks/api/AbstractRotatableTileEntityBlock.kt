package net.cydhra.technocracy.foundation.model.blocks.api

import net.cydhra.technocracy.foundation.model.blocks.color.IBlockColor
import net.cydhra.technocracy.foundation.model.blocks.util.IDynamicBlockStateContainer
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState


abstract class AbstractRotatableTileEntityBlock(unlocalizedName: String,
                                                registryName: String = unlocalizedName,
                                                colorMultiplier: IBlockColor? = null,
                                                material: Material, renderLayer: BlockRenderLayer? = null)
    : AbstractTileEntityBlock(unlocalizedName, registryName, colorMultiplier, material, renderLayer = renderLayer), IDynamicBlockStateContainer {

    companion object {
        /**
         * The block property used to determine block rotation. It is mapped to blockstate.
         */
        val facingProperty = BlockHorizontal.FACING!!
    }

    init {
        defaultState = this.blockState.baseState.withProperty(facingProperty, EnumFacing.NORTH)
    }

    override fun addExtendedPropertyToState(state: IExtendedBlockState, world: IBlockAccess?, pos: BlockPos?): IExtendedBlockState {
        return state
    }

    override fun addPropertyToState(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): IBlockState {
        return state
    }

    override fun addPropertyToBuilder(builder: BlockStateContainer.Builder): BlockStateContainer.Builder {
        return builder
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        return state.withProperty(facingProperty, rot.rotate(EnumFacing.NORTH))
    }

    override fun createBlockState(): BlockStateContainer {
        return addPropertyToBuilder(BlockStateContainer.Builder(this).add(facingProperty)).build()
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): IBlockState {
        if (state is IExtendedBlockState)
            return addExtendedPropertyToState(state, world, pos)
        return super.getExtendedState(state, world, pos)
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getStateFromMeta(meta: Int): IBlockState {
        var facing = EnumFacing.getFront(meta)
        if (facing.axis == EnumFacing.Axis.Y) facing = EnumFacing.NORTH
        return this.defaultState.withProperty(facingProperty, facing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(facingProperty) as EnumFacing).index
    }

    override fun getStateForPlacement(worldIn: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
        return addPropertyToState(defaultState.withProperty(facingProperty, placer.adjustedHorizontalFacing
                .opposite), worldIn, pos)
    }
}