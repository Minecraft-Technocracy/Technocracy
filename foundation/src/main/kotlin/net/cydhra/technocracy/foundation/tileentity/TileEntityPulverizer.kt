package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.capabilities.inventory.DynamicInventoryHandler
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.tileentity.components.InventoryComponent
import net.cydhra.technocracy.foundation.tileentity.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.tileentity.management.TEInventoryProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 * A tile entity linked to a pulverizer block that can store up to two stacks of items and processes the first stack
 * into its output (second) stack, if the processing output and second stack can be merged.
 */
class TileEntityPulverizer : AbstractMachine(), TEInventoryProvider {

    /**
     * Inventory machine component with input stack in slot 0 and output stack in slot 1
     */
    private val inputInventoryComponent = InventoryComponent(1, this, EnumFacing.WEST)

    private val outputInventoryComponent = InventoryComponent(1, this, EnumFacing.EAST)

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(outputInventoryComponent, "output_inventory")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.PULVERIZER,
                inputInventory = this.inputInventoryComponent.inventory,
                outputInventory = this.outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                machineUpgrades = this.machineUpgradesComponent,
                baseTickEnergyCost = 10))
    }

    override fun isItemValid(inventory: DynamicInventoryHandler, slot: Int, stack: ItemStack): Boolean {
        return inventory == inputInventoryComponent.inventory
    }
}