package net.cydhra.technocracy.foundation.client.gui.multiblock

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart

open class MultiblockContainer(val machine: TileEntityMultiBlockPart<*>) : TCContainer(
        ((machine.multiblockController as BaseMultiBlock).getComponents()
                .filter { (name, component) -> component is InventoryTileEntityComponent && name.contains("input") }
                .elementAtOrNull(0)?.second as? InventoryTileEntityComponent)?.inventory?.slots ?: 0,
        ((machine.multiblockController as BaseMultiBlock).getComponents()
                .filter { (name, component) -> component is InventoryTileEntityComponent && name.contains("output") }
                .elementAtOrNull(0)?.second as? InventoryTileEntityComponent)?.inventory?.slots ?: 0
)