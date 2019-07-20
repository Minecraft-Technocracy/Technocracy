package net.cydhra.technocracy.foundation.client.gui.handler

import net.cydhra.technocracy.foundation.tileentity.api.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
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
        return when (ID) {
            machineGui -> {
                val machine = world.getTileEntity(BlockPos(x, y, z)) as TCTileEntityGuiProvider
                machine.getGui(player).container
            }
            multiblockGui -> {
                val obj = world.getTileEntity(BlockPos(x, y, z))
                if(obj is TileEntityMultiBlockPart<*>) {
                    obj.getGui(player).container
                } else null
            }
            else -> null
        }
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        return when (ID) {
            machineGui -> {
                val machine = world.getTileEntity(BlockPos(x, y, z)) as TCTileEntityGuiProvider
                machine.getGui(player)
            }
            multiblockGui -> {
                val obj = world.getTileEntity(BlockPos(x, y, z))
                if(obj is TileEntityMultiBlockPart<*>) {
                    obj.getGui(player)
                } else null
            }
            else -> null
        }
    }
}
