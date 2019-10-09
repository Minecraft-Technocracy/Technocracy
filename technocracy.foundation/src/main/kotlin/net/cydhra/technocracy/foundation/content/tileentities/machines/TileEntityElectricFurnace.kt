package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 * Electric furnace tile entity
 */
class TileEntityElectricFurnace : MachineTileEntity(), TEInventoryProvider {

    /**
     * Input inventory for the furnace with one slot
     */
    private val inputInventoryComponent = InventoryComponent(1, this, EnumFacing.WEST)

    /**
     * Output inventory for the furnace with one slot
     */
    private val outputInventoryComponent = InventoryComponent(1, this, EnumFacing.EAST)

    /**
     * All recipes of the pulverizer; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.ELECTRIC_FURNACE) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(outputInventoryComponent, "output_inventory")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.ELECTRIC_FURNACE,
                inputInventory = this.inputInventoryComponent.inventory,
                outputInventory = this.outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                machineUpgrades = this.machineUpgradesComponent,
                baseTickEnergyCost = 20,
                progress = this.progressComponent))
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return inventory == inputInventoryComponent.inventory && this.recipes.any { it.getInput()[0].test(stack) }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack) {
    }

}