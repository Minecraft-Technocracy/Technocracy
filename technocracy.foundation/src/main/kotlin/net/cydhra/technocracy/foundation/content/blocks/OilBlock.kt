package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.content.fluids.mineralOilFluid
import net.cydhra.technocracy.foundation.content.potions.oilyEffect
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.PotionEffect
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.IFluidBlock


class OilBlock : BaseLiquidBlock(mineralOilFluid, "mineral_oil", Material.WATER) {
    override fun onEntityCollidedWithBlock(worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity) {
        handleMaterialAcceleration(worldIn, entityIn.entityBoundingBox, this.blockMaterial, entityIn)
    }

    // forge deprecates this because they don't want you to call it. This is not how you use
    // deprecation but the folks at forge don't care for valid software design anyway, so I will just suppress this
    // garbage.
    @Suppress("DEPRECATION")
    override fun shouldSideBeRendered(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        val neighbor = world.getBlockState(pos.offset(side))
        if ((neighbor.block is IFluidBlock || neighbor.block is BlockLiquid) && neighbor.block != this) {
            return true
        }
        return super.shouldSideBeRendered(state, world, pos, side)
    }

    //TODO add entity move event with core mod for usable water physic

    fun handleMaterialAcceleration(worldIn: World, bb: AxisAlignedBB, materialIn: Material, entityIn: Entity) {
        val j2 = MathHelper.floor(bb.minX)
        val k2 = MathHelper.ceil(bb.maxX)
        val l2 = MathHelper.floor(bb.minY)
        val i3 = MathHelper.ceil(bb.maxY)
        val j3 = MathHelper.floor(bb.minZ)
        val k3 = MathHelper.ceil(bb.maxZ)

        var found = false

        var vec3d = Vec3d.ZERO
        val blockPool = BlockPos.PooledMutableBlockPos.retain()

        for (l3 in j2 until k2) {
            for (i4 in l2 until i3) {
                for (j4 in j3 until k3) {
                    blockPool.setPos(l3, i4, j4)
                    val state = worldIn.getBlockState(blockPool)
                    val block = state.block

                    if (state.material === materialIn) {
                        val d0 = ((i4 + 1).toFloat() - BlockLiquid.getLiquidHeightPercent((state.getValue<Int>(BlockLiquid.LEVEL) as Int).toInt())).toDouble()

                        if (i3.toDouble() >= d0) {
                            vec3d = block.modifyAcceleration(worldIn, blockPool, entityIn, vec3d)
                            found = true
                        }
                    }
                }
            }
        }

        blockPool.release()

        if ((vec3d.lengthVector() > 0.0 || found) && entityIn.isPushedByWater) {
            vec3d = vec3d.normalize()
            val d1 = 0.014
            //entityIn.motionX *= vec3d.xCoord * 0.014
            //entityIn.motionY *= vec3d.yCoord * 0.014
            //entityIn.motionZ *= vec3d.zCoord * 0.014

            entityIn.motionX *= 0.8
            entityIn.motionY = -0.01
            entityIn.motionZ *= 0.8

            if (entityIn is EntityLivingBase) {
                if (entityIn is EntityPlayer) {
                    if (entityIn.isCreative)
                        return
                }

                entityIn.addPotionEffect(PotionEffect(oilyEffect, 20 * 45, 0, true, true))
            }
        }

    }

}