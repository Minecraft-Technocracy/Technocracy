package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


abstract class BaseTileEntityBlock : AbstractBaseBlock, ITileEntityProvider {

    companion object {
        val FACING = BlockHorizontal.FACING
    }

    constructor(unlocalizedName: String, registryName: String, colorMultiplier: ConstantBlockColor?, material: Material)
            : super(unlocalizedName, material, registryName, colorMultiplier)

    constructor(unlocalizedName: String, material: Material) : super(unlocalizedName, material = material)

    init {
        defaultState = this.blockState.baseState.withProperty(FACING, EnumFacing.NORTH)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) {
            //TODO open gui
            //playerIn.openGui()
        }
        return true
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var facing = EnumFacing.getFront(meta)
        if (facing.axis == EnumFacing.Axis.Y) facing = EnumFacing.NORTH
        return this.defaultState.withProperty(FACING, facing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING) as EnumFacing).index
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.adjustedHorizontalFacing.opposite))
    }
}