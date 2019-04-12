package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.cydhra.technocracy.foundation.blocks.general.AbstractRotateableBlock.Companion.facingProperty
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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
abstract class AbstractRotateableBlock(unlocalizedName: String,
                                       material: Material,
                                       registryName: String = unlocalizedName,
                                       colorMultiplier: ConstantBlockColor? = null)
    : AbstractBaseBlock(unlocalizedName, material, registryName, colorMultiplier) {

    companion object {
        /**
         * The block property used to determine block rotation. It is mapped to blockstate.
         */
        val facingProperty = BlockHorizontal.FACING!!
    }

    init {
        defaultState = this.blockState.baseState.withProperty(facingProperty, EnumFacing.NORTH)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, facingProperty)
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

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        // set the block facing towards the placing player
        worldIn.setBlockState(pos, state.withProperty(facingProperty, placer.adjustedHorizontalFacing.opposite))
    }
}