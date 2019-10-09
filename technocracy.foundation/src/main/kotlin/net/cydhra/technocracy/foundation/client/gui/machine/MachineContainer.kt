package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryComponent

open class MachineContainer(val machine: MachineTileEntity) : TCContainer(
        (machine.getComponents().filter { (name, component) -> component is InventoryComponent && name.contains("input") }
                .elementAtOrNull(0)?.second as? InventoryComponent)?.inventory?.slots ?: 0,
        (machine.getComponents().filter { (name, component) -> component is InventoryComponent && name.contains("output") }
                .elementAtOrNull(0)?.second as? InventoryComponent)?.inventory?.slots ?: 0
)