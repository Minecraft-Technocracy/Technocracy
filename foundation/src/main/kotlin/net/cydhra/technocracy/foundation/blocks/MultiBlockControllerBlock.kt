package net.cydhra.technocracy.foundation.blocks

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.api.TCMultiBlock
import net.cydhra.technocracy.foundation.blocks.general.BaseRotateableTileEntityBlock
import net.cydhra.technocracy.foundation.blocks.general.MultiBlockBaseDelegate
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.tileentity.api.TCControllerTileEntity
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World

class MultiBlockControllerBlock<out T>(name: String, tileEntityConstructor: () -> T)
    : BaseRotateableTileEntityBlock(name, material = Material.ROCK),
        TCMultiBlock<T> by MultiBlockBaseDelegate<T>(tileEntityConstructor)
        where T : TileEntity, T : TCControllerTileEntity, T : IMultiblockPart {

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {
            if (!worldIn.isRemote) {
                val controllerTileEntity = getMultiBlockPartTileEntity(worldIn, pos)
                val multiBlockController = controllerTileEntity.multiblockController
                if (controllerTileEntity.validateStructure()) {
                    playerIn.openGui(TCFoundation, TCGuiHandler.machineGui, worldIn, pos.x, pos.y, pos.z)
                } else {
                    println((controllerTileEntity as IMultiblockPart).multiblockController.numConnectedBlocks)
                    playerIn.sendMessage(multiBlockController.lastError?.chatMessage
                            ?: TextComponentTranslation("null"))
                }
            }

            return true
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }
}