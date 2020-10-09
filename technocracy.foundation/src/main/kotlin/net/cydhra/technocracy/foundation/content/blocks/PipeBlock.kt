package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.cydhra.technocracy.foundation.content.items.FacadeItem
import net.cydhra.technocracy.foundation.content.items.PipeItem
import net.cydhra.technocracy.foundation.content.items.WrenchItem
import net.cydhra.technocracy.foundation.content.items.pipeItem
import net.cydhra.technocracy.foundation.content.tileentities.pipe.TileEntityPipe
import net.cydhra.technocracy.foundation.model.blocks.api.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.util.facade.FacadeStack
import net.cydhra.technocracy.foundation.util.facade.extras.workbench.InterfaceFacadeCraftingTable
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.Block
import net.minecraft.block.BlockRedstoneOre
import net.minecraft.block.BlockWorkbench
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.properties.PropertyInteger
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
import net.minecraft.world.IWorldNameable
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import team.chisel.ctm.api.IFacade
import java.util.function.BiFunction
import java.util.function.Supplier
import kotlin.math.max


@Optional.Interface(iface = "team.chisel.ctm.api.IFacade", modid = "ctm")
class PipeBlock : AbstractTileEntityBlock("pipe", material = Material.PISTON), IFacade {

    init {
        setHardness(1.5F)
        setResistance(10.0F)
    }

    override val generateItem: Boolean
        get() = false

    var lastFallLoc: BlockPos? = null

    override fun harvestBlock(worldIn: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack) {
        player.addExhaustion(0.005f)
        if (!worldIn.isRemote) {
            if (te is TileEntityPipe) {
                for (pipe in te.getInstalledTypes()) {
                    spawnAsEntity(worldIn, pos, ItemStack(pipeItem, 1, pipe.ordinal))
                }
                for (facade in te.getFacades().values) {
                    spawnAsEntity(worldIn, pos, facade.copy().apply { this.count = 1 })
                }
            }
        }
        //Set it to air like the flower pot's harvestBlock method
        worldIn.setBlockToAir(pos)
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): IExtendedBlockState {
        return (state as IExtendedBlockState).withProperty(POSITION, pos)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(POSITION).build()
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityPipe()
    }

    override fun onBlockDestroyedByExplosion(worldIn: World, pos: BlockPos, explosionIn: Explosion) {
        val tileEntity = worldIn.getTileEntity(pos) as TileEntityPipe?
        tileEntity?.removeTileEntity()
        super.onBlockDestroyedByExplosion(worldIn, pos, explosionIn)
    }

    override fun onBlockDestroyedByPlayer(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileEntity = worldIn.getTileEntity(pos) as TileEntityPipe?
        tileEntity?.removeTileEntity()
        super.onBlockDestroyedByPlayer(worldIn, pos, state)
    }

    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer, willHarvest: Boolean): Boolean {
        val tileEntity = world.getTileEntity(pos) as TileEntityPipe?
        tileEntity?.removeTileEntity()
        return super.removedByPlayer(state, world, pos, player, willHarvest)
    }

    override fun onBlockExploded(world: World, pos: BlockPos, explosion: Explosion) {
        val tileEntity = world.getTileEntity(pos) as TileEntityPipe?
        tileEntity?.removeTileEntity()
        super.onBlockExploded(world, pos, explosion)
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (type in PipeType.values()) {
            items.add(ItemStack(this, 1, type.ordinal))
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
        return true
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {

        val tile = worldIn.getTileEntity(pos) as? TileEntityPipe ?: return false

        if (!playerIn.isSneaking) {
            val pair = getBlockOnFacing(tile, facing)
            if (pair != null && pair.first is BlockWorkbench) {
                if (!worldIn.isRemote) {
                    playerIn.displayGui(InterfaceFacadeCraftingTable(worldIn, pos))
                    //playerIn.addStat(StatList.CRAFTING_TABLE_INTERACTION)
                }
                return true
            }
        }

        val stack = playerIn.getHeldItem(hand)

        //TODO wrench check
        if (playerIn.isSneaking && stack.item is WrenchItem) {
            val mode = (stack.item as WrenchItem).getWrenchMode(stack)

            if (mode.allowedPipe != null) {
                if (tile.hasPipeType(mode.allowedPipe)) {
                    tile.removePipeType(mode.allowedPipe)
                    spawnAsEntity(worldIn, pos, ItemStack(pipeItem, 1, mode.allowedPipe.ordinal))
                }
                return true
            }

            if (mode == WrenchItem.WrenchMode.DEFAULT) {
                val attrib = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).attributeValue
                val dist = if (playerIn.isCreative) attrib else attrib - 0.5

                val raytrace = playerIn.rayTrace(dist, 1.0f)

                if (raytrace == null || raytrace.typeOfHit == RayTraceResult.Type.MISS)
                    return false

                val lookingat = getPickBlock(state, raytrace, worldIn, pos, playerIn)

                if (lookingat == null || lookingat.isEmpty)
                    return false

                if (lookingat.item is FacadeItem) {
                    val startPos = Vec3d(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ)
                    val endPos = startPos.addVector(playerIn.lookVec.x * dist, playerIn.lookVec.y * dist, playerIn.lookVec.z * dist)
                    val triple = rayTraceBestBB(startPos, endPos, tile.getPipeModelParts(), pos)
                    tile.removeFacadeOnSide(triple!!.first.first)
                } else if (lookingat.item is PipeItem) {
                    tile.removePipeType(PipeType[lookingat.metadata])
                }

                spawnAsEntity(worldIn, pos, lookingat)

                return true
            }
        }

        if (!playerIn.isSneaking && playerIn.inventory.getCurrentItem().item == Item.getItemFromBlock(this)) return true




        if (playerIn.isSneaking && playerIn.inventory.getCurrentItem().isEmpty && hand == EnumHand.MAIN_HAND) {

            //todo raytrace
            //todo chck wrench
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

        return rayTraceBestBB(startPos, endPos, list) ?: TileEntityPipe.node.offset(0.0, -999999.0, 0.0)
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
                return tile.getFacades()[triple.first.first] ?: ItemStack.EMPTY
            }
        }
        return super.getPickBlock(state, target, world, pos, player)
    }

    fun rayTraceBestBB(start: Vec3d, end: Vec3d, boundingBoxes: List<Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, TileEntityPipe.BoxType>>, offset: BlockPos): Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, TileEntityPipe.BoxType>? {
        if (boundingBoxes.isEmpty()) return null

        var bestTriple: Triple<Pair<EnumFacing, AxisAlignedBB>, PipeType?, TileEntityPipe.BoxType> = boundingBoxes[0]
        val tmpbb = bestTriple.first.second.offset(offset)
        var distance = tmpbb.calculateIntercept(start, end)?.hitVec?.distanceTo(start) ?: Double.MAX_VALUE

        for (triple in boundingBoxes) {
            val rayTraceResult = triple.first.second.offset(offset).calculateIntercept(start, end)
            if (rayTraceResult != null) {
                val d7 = rayTraceResult.hitVec.distanceTo(start)
                if (d7 < distance) {
                    bestTriple = triple
                    distance = d7
                }
            }
        }
        return bestTriple
    }

    fun rayTraceBestBB(start: Vec3d, end: Vec3d, boundingBoxes: List<AxisAlignedBB>): AxisAlignedBB? {
        if (boundingBoxes.isEmpty()) return null

        var bestbb: AxisAlignedBB = boundingBoxes[0]
        var distance = bestbb.calculateIntercept(start, end)?.hitVec?.distanceTo(start) ?: Double.MAX_VALUE

        for (bb in boundingBoxes) {
            val rayTraceResult = bb.calculateIntercept(start, end)
            if (rayTraceResult != null) {
                val curr = rayTraceResult.hitVec.distanceTo(start)
                if (curr < distance) {
                    bestbb = bb
                    distance = curr
                }
            }
        }
        return bestbb
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

        for (bb in list) {
            val ray = bb.calculateIntercept(startIn, endIn)
            if (ray != null && (result == null || result.hitVec.squareDistanceTo(startIn) > ray.hitVec.squareDistanceTo(startIn)))
                result = ray
        }

        return if (result == null) null else RayTraceResult(result.hitVec.addVector(pos.x.toDouble(),
                pos.y.toDouble(), pos.z.toDouble()), result.sideHit, pos)
    }

    override fun getEnchantPowerBonus(world: World, pos: BlockPos): Float {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return 0f
        return getMaxValue(tile, valueGetter = BiFunction { b, _ -> b.getEnchantPowerBonus(world, pos) })
    }

    override fun getSlipperiness(state: IBlockState, world: IBlockAccess, pos: BlockPos, entity: Entity?): Float {
        val tile = world.getTileEntity(pos) as? TileEntityPipe
                ?: return super.getSlipperiness(state, world, pos, entity)

        return getMaxValue(tile, arrayOf(EnumFacing.UP),
                BiFunction { t, stack -> t.getSlipperiness(t.getStateFromMeta(stack.stack.itemDamage), world, pos, entity) },
                Supplier { super.getSlipperiness(state, world, pos, entity) })
    }

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion): Float {
        val tile = world.getTileEntity(pos) as? TileEntityPipe
                ?: return super.getExplosionResistance(world, pos, exploder, explosion)

        return getMaxValue(tile, valueGetter = BiFunction { b, _ -> b.getExplosionResistance(world, pos, exploder, explosion) })
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return 0

        return getMaxValue(tile, valueGetter = BiFunction { b, stack -> b.getLightValue(b.getStateFromMeta(stack.stack.itemDamage)).toFloat() }).toInt()
    }

    override fun canBeConnectedTo(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return false
        val pair = getBlockOnFacing(tile, facing) ?: return false
        return pair.first.canBeConnectedTo(world, pos, facing)
    }

    override fun getBlockFaceShape(world: IBlockAccess, state: IBlockState, pos: BlockPos, side: EnumFacing): BlockFaceShape {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return BlockFaceShape.UNDEFINED
        val pair = getBlockOnFacing(tile, side) ?: return BlockFaceShape.UNDEFINED
        return pair.first.getBlockFaceShape(world, pair.first.getStateFromMeta(pair.second.stack.itemDamage), pos, side)
    }

    override fun onEntityWalk(world: World, pos: BlockPos, entityIn: Entity) {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return
        val pair = getBlockOnFacing(tile, EnumFacing.UP) ?: return
        if (pair.first is BlockRedstoneOre) return
        pair.first.onEntityWalk(world, pos, entityIn)
        //normaly is only called when player is inside hitbox of the block
        pair.first.onEntityCollidedWithBlock(world, pos, pair.first.getStateFromMeta(pair.second.stack.itemDamage), entityIn)
        return
    }

    override fun onFallenUpon(world: World, pos: BlockPos, entityIn: Entity, fallDistance: Float) {
        lastFallLoc = pos
        val tile = world.getTileEntity(pos) as? TileEntityPipe
                ?: return super.onFallenUpon(world, pos, entityIn, fallDistance)
        val pair = getBlockOnFacing(tile, EnumFacing.UP)
                ?: return super.onFallenUpon(world, pos, entityIn, fallDistance)
        //can use current pos as onLanded is called in same method
        return pair.first.onFallenUpon(world, pos, entityIn, fallDistance)
    }

    override fun onLanded(world: World, entityIn: Entity) {
        if (lastFallLoc == null) return

        val tile = world.getTileEntity(lastFallLoc!!) as? TileEntityPipe ?: return super.onLanded(world, entityIn)
        val pair = getBlockOnFacing(tile, EnumFacing.UP) ?: return super.onLanded(world, entityIn)
        return pair.first.onLanded(world, entityIn)
    }

    override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing): Boolean {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return true
        val pair = getBlockOnFacing(tile, side) ?: return false
        return pair.first.canPlaceBlockOnSide(world, pos, side)
    }

    override fun canPlaceTorchOnTop(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return false
        val pair = getBlockOnFacing(tile, EnumFacing.UP) ?: return false
        return pair.first.canPlaceTorchOnTop(pair.first.getStateFromMeta(pair.second.stack.itemDamage), world, pos)
    }

    fun getMaxValue(tile: TileEntityPipe, faces: Array<EnumFacing> = EnumFacing.values(), valueGetter: BiFunction<in Block, in FacadeStack, out Float>, fallback: Supplier<Float> = Supplier { 0f }): Float {
        var max = 0f
        var used = false

        for (facing in faces) {
            val pair = getBlockOnFacing(tile, facing) ?: continue
            max = max(valueGetter.apply(pair.first, pair.second), max)
            used = true
        }
        return if (used) max else fallback.get()
    }

    fun getBlockOnFacing(tile: TileEntityPipe, facing: EnumFacing): Pair<Block, FacadeStack>? {
        val stack = tile.getFacades()[facing] ?: return null
        if (stack.isEmpty) return null
        val facade = stack.item as FacadeItem
        val facadeStack = facade.getFacadeFromStack(stack)
        return getBlockFromItem(facadeStack.stack.item) to facadeStack
    }

    override fun getFacade(world: IBlockAccess, pos: BlockPos, side: EnumFacing?): IBlockState {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return defaultState
        val pair = side?.let { getBlockOnFacing(tile, it) } ?: return defaultState
        return pair.first.getStateFromMeta(pair.second.stack.itemDamage)
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        val tileEntity = worldIn.getTileEntity(pos) as TileEntityPipe
        tileEntity.onNeighborChange(worldIn, pos, fromPos)

        super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
    }
}