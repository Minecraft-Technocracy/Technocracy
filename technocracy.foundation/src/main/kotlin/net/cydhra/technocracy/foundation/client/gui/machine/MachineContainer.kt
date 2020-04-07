package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.entity.player.EntityPlayer

open class MachineContainer(machine: MachineTileEntity) : TCContainer(machine
        /*(machine.getComponents().filter { (name, component) -> component is InventoryTileEntityComponent && name.contains("input") }
                .elementAtOrNull(0)?.second as? InventoryTileEntityComponent)?.inventory?.slots ?: 0,
        (machine.getComponents().filter { (name, component) -> component is InventoryTileEntityComponent && name.contains("output") }
                .elementAtOrNull(0)?.second as? InventoryTileEntityComponent)?.inventory?.slots ?: 0*/) {
    init {
        tileEntity = machine
    }
}