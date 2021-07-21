package net.cydhra.technocracy.astronautics.content.blocks

import net.cydhra.technocracy.foundation.content.blocks.AbstractBaseBlock
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class ScaffoldBlock : AbstractBaseBlock("scaffold", Material.IRON) {
    init {
        setHardness(0.5f)
    }

    val AABB = AxisAlignedBB(0.001, 0.0, 0.001, 0.9989, 1.0, 0.9989)
    val a = AxisAlignedBB(0.0, 0.0, 0.0, 0.1875, 1.0, 1.0)

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
        return AABB
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    fun getTopMostBlock(worldIn: World, start: BlockPos): BlockPos {
        var i = 0
        while (i < 64 && worldIn.getBlockState(start.add(0, i, 0)).block == this) {
            i++;
        }
        val pos = start.add(0, i, 0)
        val state = worldIn.getBlockState(pos)
        if (state.block.isAir(state, worldIn, start.add(0, i, 0)) && i != 64) {
            return pos
        }
        return start
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val item = playerIn.getHeldItem(hand)

        if (!item.isEmpty && item.item == ItemBlock.getItemFromBlock(this)) {

            val top = getTopMostBlock(worldIn, pos)
            if (top != pos) {

                worldIn.setBlockState(top, defaultState)

                if (!playerIn.isCreative)
                    item.shrink(1)

                return true
            }
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    override fun onEntityCollidedWithBlock(worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity) {
        if (entityIn is EntityLivingBase) {
            entityIn.fallDistance = 0.0f
            val limit = 0.15
            entityIn.motionX = MathHelper.clamp(entityIn.motionX, -limit, limit)
            entityIn.motionZ = MathHelper.clamp(entityIn.motionZ, -limit, limit)
            if (entityIn.collidedHorizontally) {
                entityIn.motionY = 0.3
            } else if (entityIn.isSneaking && entityIn is EntityPlayer) {
                if (entityIn.isInWater()) {
                    entityIn.motionY = 0.02
                } else {
                    entityIn.motionY = 0.08
                }
            } else {
                entityIn.motionY = Math.max(entityIn.motionY, -0.15)
            }
        }
    }

    override fun isLadder(state: IBlockState, world: IBlockAccess, pos: BlockPos, entity: EntityLivingBase): Boolean {
        return true
    }
}