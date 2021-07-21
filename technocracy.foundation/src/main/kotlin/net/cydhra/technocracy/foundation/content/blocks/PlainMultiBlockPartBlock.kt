package net.cydhra.technocracy.foundation.content.blocks

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.cydhra.technocracy.foundation.api.blocks.TCMultiBlock
import net.cydhra.technocracy.foundation.api.tileentities.TCMultiBlockActiveTileEntity
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
 * @param renderLayer render layer of blocks of this type
 */
open class PlainMultiBlockPartBlock<T>(
        unlocalizedName: String,
        tileEntityConstructor: () -> T,
        private val glassSides: Boolean = false,
        renderLayer: BlockRenderLayer = BlockRenderLayer.SOLID)
    : AbstractTileEntityBlock(unlocalizedName, material = Material.ROCK, renderLayer = renderLayer),
        TCMultiBlock<T> by MultiBlockBaseDelegate<T>(tileEntityConstructor)
        where T : TileEntity, T : TCMultiBlockActiveTileEntity, T : IMultiblockPart {

    var opaque: Boolean = true
        set(value) {
            field = value
            lightOpacity = if (value) 255 else 0
        }
    var isFullCube: Boolean = true

    val init: Boolean

    init {
        init = true
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return !init || this.opaque
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return this.isFullCube
    }

    // for some (probably stupid) reason, forge uses deprecation to notify people about functions not to call, not
    // because they are going to be removed, but for different reasons. This is fine though
    @Suppress("DEPRECATION")
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

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack(this)
    }
}