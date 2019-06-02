package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.api.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.pipes.Network
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.streams.toList


class PipeBlock : AbstractTileEntityBlock("pipe", material = Material.PISTON) {

    companion object {
        var PIPETYPE: PropertyEnum<PipeType> = PropertyEnum.create("pipetype", PipeType::class.java)
    }

    init {
        setHardness(1.5F)
        setResistance(10.0F)
    }

    override val generateItem: Boolean
        get() = false

    override fun getExtendedState(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): IExtendedBlockState {
        return (state as IExtendedBlockState).withProperty(POSITION, pos)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(PIPETYPE).add(POSITION).build()
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityPipe(meta)
    }

    override fun onBlockDestroyedByExplosion(worldIn: World, pos: BlockPos, explosionIn: Explosion) {
        Network.removeNodeInEveryNetwork(pos, worldIn)
        super.onBlockDestroyedByExplosion(worldIn, pos, explosionIn)
    }

    override fun onBlockDestroyedByPlayer(worldIn: World, pos: BlockPos, state: IBlockState) {
        Network.removeNodeInEveryNetwork(pos, worldIn)
        super.onBlockDestroyedByPlayer(worldIn, pos, state)
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (type in PipeType.values()) {
            items.add(ItemStack(this, 1, type.ordinal))
        }
    }

    override fun onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos) {
        val wld = world as World
        val tileEntity = wld.getTileEntity(pos) as TileEntityPipe

        if (!wld.isRemote) {
            tileEntity.calculateIOPorts()
        }
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {

        if (!playerIn.isSneaking && playerIn.inventory.getCurrentItem().item == Item.getItemFromBlock(this)) return true

        if (playerIn.isSneaking && playerIn.inventory.getCurrentItem().isEmpty && hand == EnumHand.MAIN_HAND) {

            //todo raytrace
            //todo chck wrench
            if (!worldIn.isRemote) (worldIn.getTileEntity(pos) as TileEntityPipe).rotateIO()
            return false
        }

        return false
    }


    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullBlock(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos,
            side: EnumFacing): Boolean {
        return false
    }

    /**
     * Return an AABB (in world coords!) that should be highlighted when the player is targeting this Block
     */
    @SideOnly(Side.CLIENT)
    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB? {
        val length = Minecraft.getMinecraft().playerController.blockReachDistance + 1
        val entity = Minecraft.getMinecraft().player
        val startPos = Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)
        val endPos = startPos.addVector(entity.lookVec.x * length, entity.lookVec.y * length, entity.lookVec.z * length)

        val list = (worldIn.getTileEntity(pos) as TileEntityPipe).getPipeModelParts().map { it.first.second.offset(pos) }.toList()

        return rayTraceBestBB(startPos, endPos, list) ?: TileEntityPipe.node.offset(pos)
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

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(PIPETYPE).ordinal
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(PIPETYPE, PipeType.values()[meta])
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB,
                                       collidingBoxes: List<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        val list = (worldIn.getTileEntity(pos) as TileEntityPipe).getPipeModelParts().map { it.first.second }.toList()

        for (bb in list) addCollisionBoxToList(pos, entityBox, collidingBoxes, bb)
    }

    override fun collisionRayTrace(blockState: IBlockState, worldIn: World, pos: BlockPos, startIn: Vec3d,
                                   endIn: Vec3d): RayTraceResult? {
        val start = startIn.subtract(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        val end = endIn.subtract(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())

        val list = (worldIn.getTileEntity(pos) as TileEntityPipe).getPipeModelParts().map { it.first.second }.toList()

        val bbresult = rayTraceBestBB(start, end, list)

        return if (bbresult == null) null else {
            val result = bbresult.calculateIntercept(start, end)
            if (result == null) null else RayTraceResult(result.hitVec.addVector(pos.x.toDouble(),
                    pos.y.toDouble(), pos.z.toDouble()), result.sideHit, pos)
        }
    }
}