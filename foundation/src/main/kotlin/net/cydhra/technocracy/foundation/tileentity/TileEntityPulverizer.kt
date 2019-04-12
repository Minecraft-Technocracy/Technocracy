package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.tileentity.components.InventoryComponent

/**
 * A tile entity linked to a pulverizer block that can store up to two stacks of items and processes the first stack
 * into its output (second) stack, if the processing output and second stack can be merged.
 */
class TileEntityPulverizer : AbstractMachine() {

    /**
     * Inventory machine component with input stack in slot 0 and output stack in slot 1
     */
    private val inventoryComponent = InventoryComponent(2)

    init {
        this.registerComponent(inventoryComponent)
    }

    override fun update() {}
}