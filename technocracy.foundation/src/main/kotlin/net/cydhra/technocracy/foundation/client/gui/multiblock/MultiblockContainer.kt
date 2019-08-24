package net.cydhra.technocracy.foundation.client.gui.multiblock

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.multiblock.BaseMultiBlock
import net.cydhra.technocracy.foundation.tileentity.components.InventoryComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart

open class MultiblockContainer(val machine: TileEntityMultiBlockPart<*>) : TCContainer(
        ((machine.multiblockController as BaseMultiBlock).getComponents()
                .filter { (name, component) -> component is InventoryComponent && name.contains("input") }
                .elementAtOrNull(0)?.second as? InventoryComponent)?.inventory?.slots ?: 0,
        ((machine.multiblockController as BaseMultiBlock).getComponents()
                .filter { (name, component) -> component is InventoryComponent && name.contains("output") }
                .elementAtOrNull(0)?.second as? InventoryComponent)?.inventory?.slots ?: 0
)