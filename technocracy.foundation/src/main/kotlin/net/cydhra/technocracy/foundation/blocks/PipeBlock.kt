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
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState


class PipeBlock : AbstractTileEntityBlock("pipe", material = Material.PISTON) {

    companion object {
        var PIPETYPE: PropertyEnum<PipeType> = PropertyEnum.create("pipetype", PipeType::class.java)
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

    override fun getStateForPlacement(worldIn: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
        return defaultState
    }

    override fun onBlockDestroyedByExplosion(worldIn: World, pos: BlockPos, explosionIn: Explosion) {
        Network.removeNodeInEveryNetwork(pos, worldIn)
        super.onBlockDestroyedByExplosion(worldIn, pos, explosionIn)
    }

    override fun onBlockDestroyedByPlayer(worldIn: World, pos: BlockPos, state: IBlockState) {
        Network.removeNodeInEveryNetwork(pos, worldIn)
        super.onBlockDestroyedByPlayer(worldIn, pos, state)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(PIPETYPE).ordinal
    }

    override fun damageDropped(state: IBlockState): Int {
        return state.getValue(PIPETYPE).ordinal
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (type in PipeType.values()) {
            items.add(ItemStack(this, 1, type.ordinal))
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(PIPETYPE, PipeType.values()[meta])
    }

    /*override fun createBlockState(): BlockStateContainer {
        println(PIPETYPE)
        //PIPETYPE = PropertyEnum.create("pipetype", PipeType::class.java)
        return BlockStateContainer(this, PIPETYPE)
    }*/

    override fun onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos) {
        if(!(world as World).isRemote)
            (world.getTileEntity(pos) as TileEntityPipe).calculateIOPorts()
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {

        if(!playerIn.isSneaking && playerIn.inventory.getCurrentItem().item == Item.getItemFromBlock(this))
            return true

        if(playerIn.isSneaking && playerIn.inventory.getCurrentItem().isEmpty && hand == EnumHand.MAIN_HAND) {

            //todo raytrace
            //todo chck wrench
            if(!worldIn.isRemote)
                (worldIn.getTileEntity(pos) as TileEntityPipe).rotateIO()
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
}