package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


abstract class AbstractRotateableBlock(unlocalizedName: String,
                                       material: Material,
                                       registryName: String = unlocalizedName,
                                       override val colorMultiplier: ConstantBlockColor? = null)
    : AbstractBaseBlock(unlocalizedName, material, registryName, colorMultiplier) {

    companion object {
        /**
         * The block property used to determine block rotation
         */
        val facingProperty = BlockHorizontal.FACING!!
    }

    init {
        defaultState = this.blockState.baseState.withProperty(facingProperty, EnumFacing.NORTH)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, facingProperty)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var facing = EnumFacing.getFront(meta)
        if (facing.axis == EnumFacing.Axis.Y) facing = EnumFacing.NORTH
        return this.defaultState.withProperty(facingProperty, facing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(facingProperty) as EnumFacing).index
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        worldIn.setBlockState(pos, state.withProperty(facingProperty, placer.adjustedHorizontalFacing.opposite))
    }
}