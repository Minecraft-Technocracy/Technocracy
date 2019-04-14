package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.tileentity.components.InventoryComponent
import net.cydhra.technocracy.foundation.tileentity.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.tileentity.management.TEInventoryProvider
import net.minecraft.item.ItemStack

/**
 * A tile entity linked to a pulverizer block that can store up to two stacks of items and processes the first stack
 * into its output (second) stack, if the processing output and second stack can be merged.
 */
class TileEntityPulverizer : AbstractMachine(), TEInventoryProvider {

    /**
     * Inventory machine component with input stack in slot 0 and output stack in slot 1
     */
    private val inventoryComponent = InventoryComponent(2, this)

    init {
        this.registerComponent(inventoryComponent)
        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.PULVERIZER,
                inventory = this.inventoryComponent.inventory,
                inputSlots = arrayOf(0),
                outputSlots = arrayOf(1),
                energyStorage = this.energyStorageComponent.energyStorage,
                machineUpgrades = this.machineUpgradesComponent,
                baseTickEnergyCost = 10))
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return slot == 0
    }
}