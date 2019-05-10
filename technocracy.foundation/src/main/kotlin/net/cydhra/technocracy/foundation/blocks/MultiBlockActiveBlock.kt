package net.cydhra.technocracy.foundation.blocks

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.cydhra.technocracy.foundation.blocks.api.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.blocks.api.MultiBlockBaseDelegate
import net.cydhra.technocracy.foundation.blocks.api.TCMultiBlock
import net.cydhra.technocracy.foundation.tileentity.api.TCMultiBlockActiveTileEntity
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class MultiBlockActiveBlock<out T>(name: String, tileEntityConstructor: () -> T,
                                   private val renderLayer: BlockRenderLayer = BlockRenderLayer.SOLID)
    : AbstractTileEntityBlock(name, material = Material.ROCK),
        TCMultiBlock<T> by MultiBlockBaseDelegate<T>(tileEntityConstructor)
        where T : TileEntity, T : TCMultiBlockActiveTileEntity, T : IMultiblockPart {

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {
            if (!worldIn.isRemote) {
                val controllerTileEntity = getMultiBlockPartTileEntity(worldIn, pos)
                val multiBlockController = controllerTileEntity.multiblockController
                if (controllerTileEntity.validateStructure()) {
                    controllerTileEntity.onActivate(worldIn, pos, playerIn, hand, facing)
                } else {
                    playerIn.sendMessage(multiBlockController.lastError?.chatMessage
                            ?: TextComponentTranslation("null"))
                }
            }

            return true
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return this.renderLayer
    }
}