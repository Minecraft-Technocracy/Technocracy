package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.api.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.items.FacadeItem
import net.cydhra.technocracy.foundation.items.general.pipeItem
import net.cydhra.technocracy.foundation.pipes.Network
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.cydhra.technocracy.foundation.util.facade.extras.workbench.InterfaceFacadeCraftingTable
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.Block
import net.minecraft.block.BlockWorkbench
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
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
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import team.chisel.ctm.api.IFacade


@Optional.Interface(iface = "team.chisel.ctm.api.IFacade", modid = "ctm")
class PipeBlock : AbstractTileEntityBlock("pipe", material = Material.PISTON), IFacade {
    companion object {
        var PIPETYPE: PropertyEnum<PipeType> = PropertyEnum.create("pipetype", PipeType::class.java)
    }

    init {
        setHardness(1.5F)
        setResistance(10.0F)
    }

    override val generateItem: Boolean
        get() = false

    var lastFallLoc: BlockPos? = null

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos): ItemStack {
        return ItemStack(this, 1, getMetaFromState(state))
    }

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

    @SideOnly(Side.CLIENT)
    //render in every layer
    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun isTranslucent(state: IBlockState): Boolean {
        //todo only if facade is transparent
        return false
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {

        val tile = worldIn.getTileEntity(pos) as? TileEntityPipe ?: return false
        val stack = tile.getFacades()[facing]
        if (stack != null && !playerIn.isSneaking) {
            if (stack.isEmpty) return false
            val facade = stack.item as FacadeItem
            val facadeStack = facade.getFacadeFromStack(stack)
            val block = Block.getBlockFromItem(facadeStack.stack.item)

            if (block is BlockWorkbench) {

                if (worldIn.isRemote) {
                    return true
                } else {
                    playerIn.displayGui(InterfaceFacadeCraftingTable(worldIn, pos))
                    playerIn.addStat(StatList.CRAFTING_TABLE_INTERACTION)
                    return true
                }

                //val customState = block.getStateFromMeta(facadeStack.stack.itemDamage)
                //return block.onBlockActivated(FakeWorld(worldIn, customState, pos), pos, customState, playerIn, hand, facing, hitX, hitY, hitZ)
            }

        }

        if (!playerIn.isSneaking && playerIn.inventory.getCurrentItem().item == Item.getItemFromBlock(this)) return true

        if (playerIn.isSneaking && playerIn.inventory.getCurrentItem().isEmpty && hand == EnumHand.MAIN_HAND) {

            //todo raytrace
            //todo chck wrench
            if (!worldIn.isRemote) (worldIn.getTileEntity(pos) as TileEntityPipe).rotateIO()
            return false
        }

        return false
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack? {

        val length = Minecraft.getMinecraft().playerController.blockReachDistance + 1
        val entity = Minecraft.getMinecraft().player
        val startPos = Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)
        val endPos = startPos.addVector(entity.lookVec.x * length, entity.lookVec.y * length, entity.lookVec.z * length)
        val tile = world.getTileEntity(pos) as TileEntityPipe
        val map = tile.getPipeModelParts()

        val triple = rayTraceBestBB(startPos, endPos, map, pos)

        if (triple != null) {
            if (triple.second != null) {
                return ItemStack(pipeItem, 1, triple.second!!.ordinal)
            } else {
                val raytrace = collisionRayTrace(state, world, pos, startPos, endPos)
                return tile.getFacades()[raytrace!!.sideHit] ?: ItemStack.EMPTY
            }
        }
        return super.getPickBlock(state, target, world, pos, player)

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

        return rayTraceBestBB(startPos, endPos, list) ?: TileEntityPipe.node.offset(0.0, -999999.0, 0.0)
    }

    fun rayTraceBestBB(start: Vec3d, end: Vec3d, boundingBoxes: List<Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, Int>>, offset: BlockPos): Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, Int>? {
        var bestTriple: Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, Int>? = null
        var distance = 0.0
        for (triple in boundingBoxes) {
            val rayTraceResult = triple.first.second.offset(offset).calculateIntercept(start, end)
            if (rayTraceResult != null) {
                val d7 = start.squareDistanceTo(rayTraceResult.hitVec)
                if (d7 < distance || distance == 0.0) {
                    bestTriple = triple
                    distance = d7
                }
            }
        }
        return bestTriple
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

        for (bb in list) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, bb)
        }
    }

    override fun collisionRayTrace(blockState: IBlockState, worldIn: World, pos: BlockPos, startIn: Vec3d,
                                   endIn: Vec3d): RayTraceResult? {

        val list = (worldIn.getTileEntity(pos) as TileEntityPipe).getPipeModelParts().map { it.first.second.offset(pos) }.toList()

        var result: RayTraceResult? = null

        for(bb in list) {
            val ray = bb.calculateIntercept(startIn, endIn)
            if(ray != null && (result == null || result.hitVec.squareDistanceTo(startIn) > ray.hitVec.squareDistanceTo(startIn)))
                result = ray
        }

        return if (result == null) null else RayTraceResult(result.hitVec.addVector(pos.x.toDouble(),
                pos.y.toDouble(), pos.z.toDouble()), result.sideHit, pos)

        /*println("start $startIn")
        println("end $endIn")



        val bbresult = rayTraceBestBB(startIn, endIn, list)

        return if (bbresult == null) null else {
            val result = bbresult.calculateIntercept(startIn, endIn)
            if (result == null) null else RayTraceResult(result.hitVec.addVector(pos.x.toDouble(),
                    pos.y.toDouble(), pos.z.toDouble()), result.sideHit, pos)
        }*/
    }

    override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing): Boolean {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return false
        val stack = tile.getFacades()[side] ?: return false
        if (stack.isEmpty) return false
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        val block = Block.getBlockFromItem(facadeStack.stack.item)
        return block.canPlaceBlockOnSide(world, pos, side)
    }

    override fun canPlaceTorchOnTop(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return false
        val stack = tile.getFacades()[EnumFacing.UP] ?: return false
        if (stack.isEmpty) return false
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        val block = Block.getBlockFromItem(facadeStack.stack.item)
        return block.canPlaceTorchOnTop(block.getStateFromMeta(facadeStack.stack.itemDamage), world, pos)
    }

    override fun getEnchantPowerBonus(world: World, pos: BlockPos): Float {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return 0f
        var maxEnch = 0f

        for (facing in EnumFacing.values()) {
            val stack = tile.getFacades()[facing] ?: continue
            if (stack.isEmpty) continue
            val facade = stack.item as FacadeItem
            val facadeStack = facade.getFacadeFromStack(stack)
            val block = Block.getBlockFromItem(facadeStack.stack.item)
            maxEnch = Math.max(block.getEnchantPowerBonus(world, pos), maxEnch)
        }
        return maxEnch
    }

    override fun getSlipperiness(state: IBlockState, world: IBlockAccess, pos: BlockPos, entity: Entity?): Float {
        val tile = world.getTileEntity(pos) as? TileEntityPipe
                ?: return super.getSlipperiness(state, world, pos, entity)
        val stack = tile.getFacades()[EnumFacing.UP] ?: return super.getSlipperiness(state, world, pos, entity)
        if (stack.isEmpty) return super.getSlipperiness(state, world, pos, entity)
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        val block = Block.getBlockFromItem(facadeStack.stack.item)
        return block.getSlipperiness(block.getStateFromMeta(facadeStack.stack.itemDamage), world, pos, entity)
    }

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion): Float {
        val tile = world.getTileEntity(pos) as? TileEntityPipe
                ?: return super.getExplosionResistance(world, pos, exploder, explosion)
        var maxResistence = 0f

        for (facing in EnumFacing.values()) {
            val stack = tile.getFacades()[facing] ?: continue
            if (stack.isEmpty) continue
            val facade = stack.item as FacadeItem
            val facadeStack = facade.getFacadeFromStack(stack)
            val block = Block.getBlockFromItem(facadeStack.stack.item)
            maxResistence = Math.max(block.getExplosionResistance(world, pos, exploder, explosion), maxResistence)
        }
        return maxResistence
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return 0
        var maxLight = 0

        for (facing in EnumFacing.values()) {
            val stack = tile.getFacades()[facing] ?: continue
            if (stack.isEmpty) continue
            val facade = stack.item as FacadeItem
            val facadeStack = facade.getFacadeFromStack(stack)
            val block = Block.getBlockFromItem(facadeStack.stack.item)
            maxLight = Math.max(block.getLightValue(block.getStateFromMeta(facadeStack.stack.itemDamage)), maxLight)
        }
        return maxLight
    }

    override fun canBeConnectedTo(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return false
        val stack = tile.getFacades()[facing] ?: return false
        if (stack.isEmpty) return false
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        val block = Block.getBlockFromItem(facadeStack.stack.item)
        return block.canBeConnectedTo(world, pos, facing)
    }

    override fun getBlockFaceShape(world: IBlockAccess, state: IBlockState, pos: BlockPos, side: EnumFacing): BlockFaceShape {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return BlockFaceShape.UNDEFINED
        val stack = tile.getFacades()[side] ?: return BlockFaceShape.UNDEFINED
        if (stack.isEmpty) return BlockFaceShape.UNDEFINED
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        val block = Block.getBlockFromItem(facadeStack.stack.item)
        return block.getBlockFaceShape(world, block.getStateFromMeta(facadeStack.stack.itemDamage), pos, side)
    }

    override fun onEntityWalk(world: World, pos: BlockPos, entityIn: Entity) {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return
        val stack = tile.getFacades()[EnumFacing.UP] ?: return
        if (stack.isEmpty) return
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        val block = Block.getBlockFromItem(facadeStack.stack.item)
        block.onEntityWalk(world, pos, entityIn)
        //normaly is only called when player is inside hitbox of the block
        block.onEntityCollidedWithBlock(world, pos, block.getStateFromMeta(facadeStack.stack.itemDamage), entityIn)
        return
    }

    override fun onFallenUpon(world: World, pos: BlockPos, entityIn: Entity, fallDistance: Float) {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return
        val stack = tile.getFacades()[EnumFacing.UP] ?: return
        if (stack.isEmpty) return
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        val block = Block.getBlockFromItem(facadeStack.stack.item)
        //can use current pos as onLanded is called in same method
        lastFallLoc = pos
        return block.onFallenUpon(world, pos, entityIn, fallDistance)
    }

    override fun onLanded(world: World, entityIn: Entity) {
        if (lastFallLoc == null) return

        val tile = world.getTileEntity(lastFallLoc!!) as? TileEntityPipe ?: return
        val stack = tile.getFacades()[EnumFacing.UP] ?: return
        if (stack.isEmpty) return
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        val block = Block.getBlockFromItem(facadeStack.stack.item)
        return block.onLanded(world, entityIn)
    }

    override fun getFacade(world: IBlockAccess, pos: BlockPos, side: EnumFacing?): IBlockState {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return defaultState
        val stack = tile.getFacades()[side] ?: return defaultState
        if (stack.isEmpty) return defaultState
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        val block = Block.getBlockFromItem(facadeStack.stack.item)
        return block.getStateFromMeta(facadeStack.stack.itemDamage)
    }
}