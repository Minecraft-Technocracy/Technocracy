package net.cydhra.technocracy.foundation.blocks

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.cydhra.technocracy.foundation.blocks.api.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.blocks.api.MultiBlockBaseDelegate
import net.cydhra.technocracy.foundation.blocks.api.TCMultiBlock
import net.cydhra.technocracy.foundation.tileentity.api.TCMultiBlockActiveTileEntity
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * A plain, non-rotatable block that creates a multiblock tile entity when placed, using the given constructor
 *
 * @param unlocalizedName registry and language name of the block
 * @param tileEntityConstructor constructor for the tile entity that is created
 * @param opaque whether this block is opaque
 * @param renderLayer render layer of blocks of this type
 */
class PlainMultiBlockPartBlock<T>(
        unlocalizedName: String,
        tileEntityConstructor: () -> T,
        private val opaque: Boolean = true,
        private val isFullCube: Boolean = true,
        private val glassSides: Boolean = false,
        private val renderLayer: BlockRenderLayer = BlockRenderLayer.SOLID)
    : AbstractTileEntityBlock(unlocalizedName, material = Material.IRON),
        TCMultiBlock<T> by MultiBlockBaseDelegate<T>(tileEntityConstructor)
        where T : TileEntity, T : TCMultiBlockActiveTileEntity, T : IMultiblockPart {

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return this.opaque
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return this.isFullCube
    }

    override fun getBlockLayer(): BlockRenderLayer {
        return this.renderLayer
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        if (glassSides) {
            val state = blockAccess.getBlockState(pos.offset(side))
            val block = state.block

            if (blockState !== state) {
                return true
            }

            if (block === this) {
                return false
            }
        }

        return super.shouldSideBeRendered(blockState, blockAccess, pos, side)
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos): ItemStack {
        return ItemStack(this)
    }
}