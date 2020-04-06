package net.cydhra.technocracy.foundation.client.gui.handler

import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.model.tileentities.api.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class TCGuiHandler : IGuiHandler {

    companion object {
        const val machineGui: Int = 0
        const val multiblockGui: Int = 1
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val te = world.getTileEntity(BlockPos(x, y, z))

        val gui = when (ID) {
            machineGui -> {
                if (te is TCTileEntityGuiProvider) {
                    te.getGui(player).container
                } else null
            }
            multiblockGui -> {
                if (te is TileEntityMultiBlockPart<*>) {
                    te.getGui(player).container
                } else null
            }
            else -> null
        }

        //gui?.tileEntity = te

        return gui
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val te = world.getTileEntity(BlockPos(x, y, z))
        val gui = when (ID) {
            machineGui -> {
                if (te is TCTileEntityGuiProvider) {
                    TCClientGuiImpl(te.getGui(player))
                } else null
            }
            multiblockGui -> {
                if (te is TileEntityMultiBlockPart<*>) {
                    TCClientGuiImpl(te.getGui(player))
                } else null
            }
            else -> null
        }

        //gui?.container?.tileEntity = te

        return gui
    }
}
