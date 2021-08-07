package net.cydhra.technocracy.foundation.content.blocks.wrapper

import net.cydhra.technocracy.foundation.api.blocks.util.IBlockStateMapper
import net.cydhra.technocracy.foundation.content.blocks.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.content.tileentities.BlockWrapperTileEntity
import net.cydhra.technocracy.foundation.util.propertys.BLOCKSTATE
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.cydhra.technocracy.foundation.util.structures.BlockInfo
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import team.chisel.ctm.api.IFacade

@Optional.Interface(iface = "team.chisel.ctm.api.IFacade", modid = "ctm")
open class BlockWrapperBlock(val name: String = "blockwrapper") :
    AbstractTileEntityBlock(name, material = Material.CLOTH), IFacade, IBlockStateMapper {

    override fun getCreativeTabToDisplayOn(): CreativeTabs? {
        return null
    }


    companion object {
        //private var RENDER_TYPE: PropertyEnum<RenderType> =
        //    PropertyEnum.create("rendertype", RenderType::class.java)

        private var OpaqueCube: PropertyBool = PropertyBool.create("opaquecube")
        private var IsInvisible: PropertyBool = PropertyBool.create("invisible")
        private var FullCube: PropertyBool = PropertyBool.create("fullcube")

        private var Translucent: PropertyBool = PropertyBool.create("translucent")
        //private var UseNeighborBrightness: PropertyBool = PropertyBool.create("nbbrightness")
        //private var CollideCheck: PropertyBool = PropertyBool.create("collidecheck")
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return if (cacheState.getValue(IsInvisible)) EnumBlockRenderType.INVISIBLE else EnumBlockRenderType.MODEL
    }

    var cacheState = defaultState

    //because minecraft is using the default state for getRenderType we need to cache the state in this method
    //as it gets called right before the render type is queried
    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        cacheState = state
        return true
    }


    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {

        val (info, _) = getData(world, pos) ?: return false

        var state = world.getBlockState(pos)
        state = state.block.getActualState(state, world, pos)
        if (state.material.isToolNotRequired) {
            return true
        }

        val stack = player.heldItemMainhand
        val tool: String? = info.block.getHarvestTool(state)
        if (stack.isEmpty || tool == null) {
            return player.canHarvestBlock(state)
        }

        val toolLevel = stack.item.getHarvestLevel(stack, tool, player, state)
        return if (toolLevel < 0) {
            player.canHarvestBlock(state)
        } else toolLevel >= info.block.getHarvestLevel(state)

    }

    private enum class RenderType(val type: String) : IStringSerializable {
        INVISIBLE("invisible"),
        LIQUID("liquid"),
        ENTITYBLOCK_ANIMATED("entityblockanimated"),
        MODEL("model");

        override fun getName(): String {
            return type
        }
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(IsInvisible).add(OpaqueCube)
            .add(FullCube).add(Translucent)/*.add(CollideCheck)*/
            /*.add(UseNeighborBrightness)*/.add(POSITION).add(BLOCKSTATE).build()
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return state.getValue(OpaqueCube)
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return state.getValue(FullCube)// super.isFullCube(state)
    }

    /*@Deprecated("")
    override fun isTopSolid(state: IBlockState): Boolean {
        return state.material.isOpaque && state.isFullCube
    }*/

    @Deprecated("")
    override fun isFullBlock(state: IBlockState?): Boolean {
        return /*state?.getValue(FullBlock) ?:*/ false
    }

    @Deprecated("")
    @SideOnly(Side.CLIENT)
    override fun isTranslucent(state: IBlockState?): Boolean {
        return state?.getValue(Translucent) ?: translucent
    }

    override fun isNormalCube(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean {

        val (info, world) = getData(world, pos) ?: return super.isNormalCube(state, world, pos)

        return info.block.isNormalCube(info.state, world, pos)
    }

    /*@Deprecated("")
    override fun getLightValue(state: IBlockState?): Int {
        return lightValue
    }*/

    @Deprecated("")
    override fun getUseNeighborBrightness(state: IBlockState?): Boolean {
        return useNeighborBrightness
        //if (state == null) return useNeighborBrightness
        //return !isOpaqueCube(state)
    }

    override fun getHarvestTool(state: IBlockState): String? {
        return null
    }

    override fun getPlayerRelativeBlockHardness(
        state: IBlockState,
        player: EntityPlayer,
        worldIn: World,
        pos: BlockPos
    ): Float {

        val (info, world) = getData(worldIn, pos) ?: return 1f

        val hardness = info.state.getBlockHardness(world, pos)
        if (hardness < 0.0f) {
            return 0.0f
        }

        return if (!canHarvestBlock(worldIn, pos, player)) {
            player.getDigSpeed(info.state, pos) / hardness / 100f
        } else {
            player.getDigSpeed(info.state, pos) / hardness / 30f
        }
    }

    override fun isPassable(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        val (info, world) = getData(worldIn, pos) ?: return super.isPassable(worldIn, pos)
        return info.block.isPassable(world, pos)
    }

    private infix fun Int.or(b: Boolean): Int {
        return this or if (b) 0x1 else 0x0
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var meta = if (state.getValue(IsInvisible)) 1 else 0
        meta = meta shl 0x1
        //meta = meta or state.getValue(UseNeighborBrightness)
        //meta = meta shl 0x1
        meta = meta or state.getValue(Translucent)
        meta = meta shl 0x1
        meta = meta or state.getValue(FullCube)
        meta = meta shl 0x1
        meta = meta or state.getValue(OpaqueCube)

        return meta
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var meta = meta

        val isOpaqueCube = meta and 0x1 != 0
        meta = meta shr 0x1

        val isFullBlock = meta and 0x1 != 0
        meta = meta shr 0x1

        val isTranslucent = meta and 0x1 != 0
        meta = meta shr 0x1

        //val getUseNeighborBrightness = meta and 0x1 != 0
        //meta = meta shr 0x1

        //val canCollideCheck = meta and 0x1 != 0
        //meta = meta shr 0x1

        val isInvis = meta and 0x1 != 0

        return this.defaultState
            .withProperty(IsInvisible, isInvis)
            .withProperty(OpaqueCube, isOpaqueCube)
            .withProperty(FullCube, isFullBlock)
            .withProperty(Translucent, isTranslucent)
        //.withProperty(UseNeighborBrightness, getUseNeighborBrightness)
        //.withProperty(CollideCheck, canCollideCheck)
    }

    override fun onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos) {
        super.onNeighborChange(world, pos, neighbor)
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        val (info, world) = getData(worldIn, pos) ?: return 1f

        return info.block.getBlockHardness(info.state, world, pos)
    }

    //we dont drop anything we delegate it to the wrapped block
    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack.EMPTY
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun harvestBlock(
        worldIn: World,
        player: EntityPlayer,
        pos: BlockPos,
        state: IBlockState,
        te: TileEntity?,
        stack: ItemStack
    ) {
        val (info, world) = getData(worldIn, pos) ?: return
        val block = info.block

        block.harvestBlock(world, player, pos, info.state, world.getTileEntity(pos), stack)

        //Set it to air like the flower pot's harvestBlock method
        worldIn.setBlockToAir(pos)
    }

    override fun getEnchantPowerBonus(world: World, pos: BlockPos): Float {
        val (info, world) = getData(world, pos) ?: return 0f

        return info.block.getEnchantPowerBonus(world, pos)
    }

    override fun getSlipperiness(state: IBlockState, world: IBlockAccess, pos: BlockPos, entity: Entity?): Float {
        val (info, world) = getData(world, pos) ?: return 0f

        return info.block.getSlipperiness(info.state, world, pos, entity)
    }

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion): Float {
        val (info, world) = getData(world, pos) ?: return super.getExplosionResistance(world, pos, exploder, explosion)

        return info.block.getExplosionResistance(world, pos, exploder, explosion)
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val (info, world) = getData(world, pos) ?: return 0

        return info.block.getLightValue(info.state, world, pos)
    }

    override fun canBeConnectedTo(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val (info, world) = getData(world, pos) ?: return false
        return info.block.canBeConnectedTo(world, pos, facing)
    }

    override fun getBlockFaceShape(
        world: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        side: EnumFacing
    ): BlockFaceShape {

        val (info, world) = getData(world, pos) ?: return BlockFaceShape.UNDEFINED

        return info.block.getBlockFaceShape(world, info.state, pos, side)
    }

    override fun onEntityWalk(world: World, pos: BlockPos, entityIn: Entity) {

        val (info, world) = getData(world, pos) ?: return

        info.block.onEntityWalk(world, pos, entityIn)
    }

    var lastFallLoc: BlockPos? = null

    override fun onFallenUpon(world: World, pos: BlockPos, entityIn: Entity, fallDistance: Float) {
        lastFallLoc = pos
        val (info, world) = getData(world, pos) ?: return
        info.block.onFallenUpon(world, pos, entityIn, fallDistance)
    }

    override fun onLanded(world: World, entityIn: Entity) {
        if (lastFallLoc == null) return

        val (info, world) = getData(world, lastFallLoc!!) ?: return

        info.block.onLanded(world, entityIn)
    }

    override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing): Boolean {

        val (info, world) = getData(world, pos) ?: return false

        return info.block.canPlaceBlockOnSide(world, pos, side)
    }

    override fun canPlaceTorchOnTop(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean {

        val (info, world) = getData(world, pos) ?: return false

        return info.block.canPlaceTorchOnTop(state, world, pos)
    }

    override fun getStateForPlacement(
        world: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase,
        hand: EnumHand
    ): IBlockState {
        val state = world.getBlockState(pos)

        var meta = if (state.renderType != EnumBlockRenderType.MODEL) 1 else 0
        meta = meta shl 0x1
        //meta = meta or state.useNeighborBrightness()
        //meta = meta shl 0x1
        meta = meta or state.isTranslucent
        meta = meta shl 0x1
        meta = meta or state.isFullCube
        meta = meta shl 0x1
        meta = meta or state.isOpaqueCube

        return super.getStateForPlacement(
            world,
            pos,
            facing,
            hitX,
            hitY,
            hitZ,
            meta,
            placer,
            hand
        )
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack? {

        val (info, world) = getData(world, pos) ?: return ItemStack.EMPTY

        return info.block.getPickBlock(info.state, target, world, pos, player)

    }

    override fun canEntityDestroy(state: IBlockState, world: IBlockAccess, pos: BlockPos, entity: Entity): Boolean {
        val (info, world) = getData(world, pos) ?: return true

        return info.block.canEntityDestroy(info.state, world, pos, entity)
    }

    override fun canCreatureSpawn(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        type: EntityLiving.SpawnPlacementType
    ): Boolean {
        val (info, world) = getData(world, pos) ?: return false

        return info.block.canCreatureSpawn(info.state, world, pos, type)
    }

    override fun doesSideBlockChestOpening(
        blockState: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        val (info, world) = getData(world, pos) ?: return true
        return info.block.doesSideBlockChestOpening(info.state, world, pos, side)
    }

    override fun doesSideBlockRendering(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        face: EnumFacing
    ): Boolean {
        val (info, world) = getData(world, pos) ?: return true

        return info.block.doesSideBlockRendering(info.state, world, pos, face)
    }

    override fun canConnectRedstone(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing?
    ): Boolean {
        val (info, world) = getData(world, pos) ?: return false

        return info.block.canConnectRedstone(info.state, world, pos, side)
    }

    override fun canSustainLeaves(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean {
        val (info, world) = getData(world, pos) ?: return false

        return info.block.canSustainLeaves(info.state, world, pos)
    }

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        val (info, world) = getData(world, pos) ?: return false
        return info.block.canSustainPlant(info.state, world, pos, direction, plantable)
    }

    override fun getLightOpacity(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val (info, world) = getData(world, pos) ?: return 0
        return info.block.getLightOpacity(info.state, world, pos)
    }

    override fun getStateAtViewpoint(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        viewpoint: Vec3d
    ): IBlockState {
        val (info, _) = getData(world, pos) ?: return defaultState

        return info.state
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): IBlockState {
        val (info, _) = getData(world!!, pos!!) ?: return state
        return (state as IExtendedBlockState).withProperty(POSITION, pos).withProperty(BLOCKSTATE, info.state)
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        val (info, world) = getData(worldIn, pos) ?: return super.getCollisionBoundingBox(blockState, worldIn, pos)

        return info.block.getCollisionBoundingBox(info.state, world, pos)
    }

    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World?, pos: BlockPos): AxisAlignedBB {
        val (info, world) = getData(worldIn, pos) ?: return super.getSelectedBoundingBox(state, worldIn, pos)
        return info.block.getSelectedBoundingBox(info.state, world, pos)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess?, pos: BlockPos): AxisAlignedBB {
        val (info, world) = getData(source, pos) ?: return super.getBoundingBox(state, source, pos)
        return info.block.getBoundingBox(info.state, world, pos)
    }

    override fun getStrongPower(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Int {

        val (info, world) = getData(blockAccess, pos) ?: return super.getStrongPower(blockState, blockAccess, pos, side)

        return info.block.getStrongPower(info.state, world, pos, side)
    }

    override fun getWeakPower(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Int {
        val (info, world) = getData(blockAccess, pos) ?: return super.getWeakPower(blockState, blockAccess, pos, side)

        return info.block.getWeakPower(info.state, world, pos, side)

    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        return super.getActualState(state, worldIn, pos)
    }

    fun getData(world: World?, pos: BlockPos): Pair<BlockInfo, World>? {
        if (world == null)
            return null
        val tile = world.getTileEntity(pos) as? BlockWrapperTileEntity
        val info = tile?.block ?: return null
        return info to tile.getWorld(world)
    }

    fun getData(world: IBlockAccess?, pos: BlockPos): Pair<BlockInfo, IBlockAccess>? {
        if (world == null)
            return null
        val tile = world.getTileEntity(pos) as? BlockWrapperTileEntity
        val info = tile?.block ?: return null
        return info to tile.getAccess(world)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return BlockWrapperTileEntity()
    }

    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        val (info, world) = getData(blockAccess, pos) ?: return true
        return info.state.shouldSideBeRendered(world, pos, side)
    }

    override fun getFacade(world: IBlockAccess, pos: BlockPos, side: EnumFacing?): IBlockState {
        val (info, _) = getData(world, pos) ?: return defaultState
        return info.state
    }

    override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
        return ModelResourceLocation(ResourceLocation("technocracy.foundation", name), "normal")
    }
}