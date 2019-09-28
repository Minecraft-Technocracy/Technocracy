package net.cydhra.technocracy.foundation.blocks.api

import net.cydhra.technocracy.foundation.blocks.color.IBlockColor
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


abstract class AbstractRotatableTileEntityBlock(unlocalizedName: String,
                                                registryName: String = unlocalizedName,
                                                colorMultiplier: IBlockColor? = null,
                                                material: Material)
    : AbstractTileEntityBlock(unlocalizedName, registryName, colorMultiplier, material) {

    companion object {
        /**
         * The block property used to determine block rotation. It is mapped to blockstate.
         */
        val facingProperty = BlockHorizontal.FACING!!
    }

    init {
        defaultState = this.blockState.baseState.withProperty(facingProperty, EnumFacing.NORTH)
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        return state.withProperty(facingProperty, rot.rotate(EnumFacing.NORTH))
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(facingProperty).build()
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
        return defaultState.withProperty(facingProperty, placer.adjustedHorizontalFacing
                .opposite)
    }
}