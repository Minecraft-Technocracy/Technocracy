package net.cydhra.technocracy.foundation.blocks.api

import net.cydhra.technocracy.foundation.blocks.api.AbstractRotatableBlock.Companion.facingProperty
import net.cydhra.technocracy.foundation.blocks.color.IBlockColor
import net.cydhra.technocracy.foundation.blocks.util.IDynamicBlockStateContainer
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState


/**
 * An implementation of [IBaseBlock] that defines bock behaviour required for blocks that can have a rotation. This
 * is especially required for most tile entities, as machines and so forth do have a front side and sided
 * configuration. However this class does not define a tile entity and stores the [facingProperty] in its [IBlockState].
 * The model is required to adapt to the block's facing.
 *
 * @param unlocalizedName the unlocalized name of the block used for language lookup.
 * @param material block material
 * @param registryName the name of the block used in registries. By default the unlocalized name is used.
 * @param colorMultiplier special color multiplier for block texture. Null by default.
 */
abstract class AbstractRotatableBlock(unlocalizedName: String,
                                      material: Material,
                                      registryName: String = unlocalizedName,
                                      colorMultiplier: IBlockColor? = null)
    : AbstractBaseBlock(unlocalizedName, material, registryName, colorMultiplier), IDynamicBlockStateContainer {

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
        return state.withProperty(AbstractRotatableTileEntityBlock.facingProperty, rot.rotate(EnumFacing.NORTH))
    }

    override fun createBlockState(): BlockStateContainer {
        return addPropertyToBuilder(BlockStateContainer.Builder(this).add(AbstractRotatableTileEntityBlock.facingProperty)).build()
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): IExtendedBlockState {
        return addExtendedPropertyToState(state as IExtendedBlockState, world, pos)
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getStateFromMeta(meta: Int): IBlockState {
        //Todo separate
        var facing = EnumFacing.getFront(meta)
        if (facing.axis == EnumFacing.Axis.Y) facing = EnumFacing.NORTH
        return this.defaultState.withProperty(facingProperty, facing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(facingProperty) as EnumFacing).index
    }

    override fun getStateForPlacement(worldIn: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
        return addPropertyToState(defaultState.withProperty(AbstractRotatableTileEntityBlock.facingProperty, placer.adjustedHorizontalFacing
                .opposite), worldIn, pos)
    }

}