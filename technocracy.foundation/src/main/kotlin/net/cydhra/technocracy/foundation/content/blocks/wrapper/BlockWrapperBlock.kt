package net.cydhra.technocracy.foundation.content.blocks.wrapper

import net.cydhra.technocracy.foundation.content.blocks.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.content.tileentities.TileBlockWrapper
import net.cydhra.technocracy.foundation.util.structures.BlockInfo
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.*
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import java.util.*

open class BlockWrapperBlock(val name: String = "blockwrapper") :
    AbstractTileEntityBlock(name, material = Material.CLOTH) {

    companion object {
        private var RENDER_LAYER: PropertyEnum<RenderLayer> =
            PropertyEnum.create("renderlayer", RenderLayer::class.java)
    }

    private enum class RenderLayer(val type: BlockRenderLayer) : IStringSerializable {
        SOLID(BlockRenderLayer.SOLID), CUTOUT_MIPPED(BlockRenderLayer.CUTOUT_MIPPED), CUTOUT(BlockRenderLayer.CUTOUT), TRANSLUCENT(
            BlockRenderLayer.TRANSLUCENT
        );

        companion object {
            fun fromId(id: Int): RenderLayer {
                return values()[MathHelper.clamp(id, 0, values().size - 1)]
            }

            fun fromLayer(id: BlockRenderLayer): RenderLayer {
                return values().first { it.type == id }
            }
        }

        override fun getName(): String {
            return this.type.name.lowercase(Locale.getDefault())
        }
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(RENDER_LAYER).build()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(RENDER_LAYER).ordinal
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(RENDER_LAYER, RenderLayer.values()[meta])
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        val tile =
            worldIn.getTileEntity(pos) as? TileBlockWrapper ?: return super.getBlockHardness(blockState, worldIn, pos)
        return tile.block?.state?.getBlockHardness(tile.getWorld(worldIn), pos) ?: super.getBlockHardness(
            blockState,
            worldIn,
            pos
        )
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getDrops(
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ): MutableList<ItemStack> {
        val (info, world) = getData(world, pos) ?: return mutableListOf()

        return info.block.getDrops(world, pos, info.state, fortune)
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

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        return state.getValue(RENDER_LAYER).type == layer
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
        return super.getStateForPlacement(
            world,
            pos,
            facing,
            hitX,
            hitY,
            hitZ,
            world.getBlockState(pos).block.blockLayer.ordinal,
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

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val (info, world) = getData(world, pos) ?: return super.getExtendedState(state, world, pos)
        return info.block.getExtendedState(info.state, world, pos)
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        val (info, world) = getData(worldIn, pos) ?: return super.getCollisionBoundingBox(blockState, worldIn, pos)

        return info.block.getCollisionBoundingBox(info.state, world, pos)
    }

    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        val (info, world) = getData(worldIn, pos) ?: return super.getSelectedBoundingBox(state, worldIn, pos)
        return info.block.getSelectedBoundingBox(info.state, world, pos)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
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

    fun getData(world: IBlockAccess, pos: BlockPos): Pair<BlockInfo, World>? {
        val tile = world.getTileEntity(pos) as? TileBlockWrapper
        val info = tile?.block ?: return null
        return info to tile.getWorld(world as World)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return TileBlockWrapper()
    }
}