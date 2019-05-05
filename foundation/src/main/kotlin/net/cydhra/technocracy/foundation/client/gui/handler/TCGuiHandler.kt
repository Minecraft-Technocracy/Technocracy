package net.cydhra.technocracy.foundation.client.gui.handler

import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class TCGuiHandler : IGuiHandler {

    companion object {
        const val machineGui: Int = 0
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val machine = world.getTileEntity(BlockPos(x, y, z)) as MachineTileEntity

        return when (ID) {
            machineGui -> machine.getGui(player).container
            else -> null
        }
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val machine = world.getTileEntity(BlockPos(x, y, z)) as MachineTileEntity

        return when (ID) {
            machineGui -> machine.getGui(player)
            else -> null
        }
    }
}
