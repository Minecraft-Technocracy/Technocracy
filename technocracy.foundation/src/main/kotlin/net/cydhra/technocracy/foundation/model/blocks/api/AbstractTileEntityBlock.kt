package net.cydhra.technocracy.foundation.model.blocks.api

import net.cydhra.technocracy.foundation.api.IWrench
import net.cydhra.technocracy.foundation.model.blocks.color.IBlockColor
import net.minecraft.block.Block
import net.minecraft.block.BlockFlowerPot
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.IWorldNameable
import net.minecraft.world.World


/**
 * An implementation of [IBaseBlock] that creates a tile entity with it. It inherits [AbstractRotatableBlock] so it
 * automatically has a block state with facing information. The class does not define behaviour on its own yet,
 * subclasses are required to implement tile entity interaction.
 *
 * @param unlocalizedName the unlocalized name of the block used for language lookup.
 * @param material block material
 * @param registryName the name of the block used in registries. By default the unlocalized name is used.
 * @param colorMultiplier special color multiplier for block texture. Null by default.
 */
abstract class AbstractTileEntityBlock(unlocalizedName: String,
                                       registryName: String = unlocalizedName,
                                       colorMultiplier: IBlockColor? = null,
                                       material: Material,
                                       renderLayer: BlockRenderLayer? = null)
    : AbstractBaseBlock(unlocalizedName, material, registryName, colorMultiplier, renderLayer = renderLayer), ITileEntityProvider {

    /**
     * Returns the ItemStack of the TileEntity if it gets destroyed
     */
    protected abstract fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack

    open fun onBlockWrenched(worldIn: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack): Boolean {
        return false
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val stack = playerIn.getHeldItem(hand)

        if (!stack.isEmpty && stack.item is IWrench) {
            return this.onBlockWrenched(worldIn, playerIn, pos, state, worldIn.getTileEntity(pos), stack)
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    /**
     * Used together with [Block.removedByPlayer].
     * This is like Vanilla's [Block.harvestBlock] except that uses the custom [ItemStack]
     * from [Block.getItemDropped]
     *
     * @author Forge
     * @see [BlockFlowerPot.harvestBlock]
     */
    override fun harvestBlock(worldIn: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack) {

        player.addStat(StatList.getBlockStats(this)!!)
        player.addExhaustion(0.005f)
        if (!worldIn.isRemote) {
            val dropItem = getDropItem(state, worldIn, pos, te)
            if (te is IWorldNameable) {
                dropItem.setStackDisplayName((te as IWorldNameable).name)
            }
            spawnAsEntity(worldIn, pos, dropItem)
        }
        //Set it to air like the flower pot's harvestBlock method
        worldIn.setBlockToAir(pos)
    }


    override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack? {
        return getDropItem(state, world, pos, world.getTileEntity(pos))
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        drops.add(getDropItem(state, world, pos, world.getTileEntity(pos)))
    }

    /**
     * Returns that this "cannot" be silk touched. This is so that [Block.getSilkTouchDrop] is not called, because only [Block.getDrops] supports tile entities.
     * Our blocks keep their inventory and other behave like they are being silk touched by default anyway.
     *
     * @return false
     */
    override fun canSilkHarvest(): Boolean {
        return false
    }

    /**
     * Keep tile entity in world until after [Block.getDrops]. Used together with [Block.harvestBlock].
     *
     * @author Forge
     * @see BlockFlowerPot.removedByPlayer
     */
    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer, willHarvest: Boolean): Boolean {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false)
    }

}