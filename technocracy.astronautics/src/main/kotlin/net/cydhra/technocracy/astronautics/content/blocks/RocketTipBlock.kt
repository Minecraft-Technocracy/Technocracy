package net.cydhra.technocracy.astronautics.content.blocks

import net.cydhra.technocracy.foundation.content.blocks.AbstractBaseBlock
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class RocketTipBlock : AbstractBaseBlock("rocket_tip", Material.IRON) {

    companion object {
        val TIP_TYPE = PropertyEnum.create("type", TipType::class.java)
    }

    init {
        setHardness(1.5f)
        this.defaultState = this.blockState.baseState.withProperty(TIP_TYPE, TipType.MIDDLE)
    }

    override fun getMobilityFlag(state: IBlockState): EnumPushReaction {
        return EnumPushReaction.BLOCK
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.offset(EnumFacing.UP)) && super.canPlaceBlockAt(worldIn, pos.offset(EnumFacing.UP, 2))
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        worldIn.setBlockState(pos, getStateFromMeta(TipType.BOTTOM.ordinal))
        worldIn.setBlockState(pos.offset(EnumFacing.UP), getStateFromMeta(TipType.MIDDLE.ordinal))
        worldIn.setBlockState(pos.offset(EnumFacing.UP, 2), getStateFromMeta(TipType.TOP.ordinal))
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(TIP_TYPE, TipType.values()[meta])
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue<TipType>(TIP_TYPE) as TipType).ordinal
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, TIP_TYPE)
    }

    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        if (getMetaFromState(blockState) == TipType.TOP.ordinal) return false
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side)
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB,
                                       collidingBoxes: List<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        for (bb in getBB(state.getValue(TIP_TYPE))) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, bb)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB? {
        val length = Minecraft.getMinecraft().playerController.blockReachDistance + 1
        val entity = Minecraft.getMinecraft().player
        val startPos = Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)
        val endPos = startPos.addVector(entity.lookVec.x * length, entity.lookVec.y * length, entity.lookVec.z * length)


        val list = getBB(state.getValue(TIP_TYPE)).map { it.offset(pos) }.toList()


        return rayTraceBestBB(startPos, endPos, list) ?: AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    override fun onBlockHarvested(worldIn: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
        when (state.getValue(TIP_TYPE)) {
            TipType.MIDDLE -> {
                worldIn.setBlockToAir(pos.offset(EnumFacing.UP))
                worldIn.setBlockToAir(pos.offset(EnumFacing.DOWN))
            }
            TipType.TOP -> {
                worldIn.setBlockToAir(pos.offset(EnumFacing.DOWN))
                worldIn.setBlockToAir(pos.offset(EnumFacing.DOWN, 2))
            }
            TipType.BOTTOM -> {
                worldIn.setBlockToAir(pos.offset(EnumFacing.UP))
                worldIn.setBlockToAir(pos.offset(EnumFacing.UP, 2))
            }
        }
    }

    override fun collisionRayTrace(blockState: IBlockState, worldIn: World, pos: BlockPos, startIn: Vec3d,
                                   endIn: Vec3d): RayTraceResult? {

        val list = getBB(blockState.getValue(TIP_TYPE)).map { it.offset(pos) }.toList()

        var result: RayTraceResult? = null

        for (bb in list) {
            val ray = bb.calculateIntercept(startIn, endIn)
            if (ray != null && (result == null || result.hitVec.squareDistanceTo(startIn) > ray.hitVec.squareDistanceTo(startIn)))
                result = ray
        }

        return if (result == null) null else RayTraceResult(result.hitVec.addVector(pos.x.toDouble(),
                pos.y.toDouble(), pos.z.toDouble()), result.sideHit, pos)
    }

    fun rayTraceBestBB(start: Vec3d, end: Vec3d, boundingBoxes: List<AxisAlignedBB>): AxisAlignedBB? {
        var bestbb: AxisAlignedBB? = null
        var distance = 0.0
        for (bb in boundingBoxes) {
            val rayTraceResult = bb.calculateIntercept(start, end)
            if (rayTraceResult != null) {
                val d7 = start.squareDistanceTo(rayTraceResult.hitVec)
                if (d7 < distance || distance == 0.0) {
                    bestbb = bb
                    distance = d7
                }
            }
        }
        return bestbb
    }

    fun getBB(type: TipType): List<AxisAlignedBB> {
        val list = mutableListOf<AxisAlignedBB>()

        val pixel = 1.0 / 16.0

        if (type == TipType.BOTTOM) {
            list.add(AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
        }
        if (type == TipType.MIDDLE) {
            list.add(AxisAlignedBB(pixel, 0.0, pixel, 1.0 - pixel, pixel * 3, 1.0 - pixel))

            var currheight = pixel * 3
            list.add(AxisAlignedBB(pixel * 2, currheight, pixel * 2, 1.0 - pixel - pixel, currheight + pixel * 4, 1.0 - pixel - pixel))
            currheight += pixel * 4
            list.add(AxisAlignedBB(pixel * 3, currheight, pixel * 3, 1.0 - pixel * 3, currheight + pixel * 6, 1.0 - pixel * 3))
            currheight += pixel * 6
            list.add(AxisAlignedBB(pixel * 5, currheight, pixel * 5, 1.0 - pixel * 5, currheight + pixel * 8, 1.0 - pixel * 5))
        }
        if (type == TipType.TOP) {
            list.add(AxisAlignedBB(pixel * 5, -pixel * 3, pixel * 5, 1.0 - pixel * 5, pixel * 5, 1.0 - pixel * 5))
            list.add(AxisAlignedBB(pixel * 6 + pixel / 2, pixel * 5, pixel * 6 + pixel / 2, 1.0 - (pixel * 6 + pixel / 2), 1.0, 1.0 - (pixel * 6 + pixel / 2)))
        }

        return list
    }

    enum class TipType : IStringSerializable {
        MIDDLE, TOP, BOTTOM;

        override fun getName(): String {
            return this.name.toLowerCase()
        }
    }
}