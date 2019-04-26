package net.cydhra.technocracy.foundation.client.gui.handler

import net.cydhra.technocracy.foundation.client.gui.machine.MachineContainer
import net.cydhra.technocracy.foundation.client.gui.machine.MachineGui
import net.cydhra.technocracy.foundation.tileentity.AbstractMachine
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class GuiHandler : IGuiHandler {

    companion object {
        val machineGui: Int = 0
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val machine = world.getTileEntity(BlockPos(x, y, z)) as AbstractMachine

        return when (ID) {
            machineGui -> MachineContainer(player.inventory, machine)
            else -> null
        }
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val machine = world.getTileEntity(BlockPos(x, y, z)) as AbstractMachine

        return when (ID) {
            machineGui -> MachineGui(machine, MachineContainer(player.inventory, machine), player.inventory, machine.getGuiTabs())
            else -> null
        }
    }
}
