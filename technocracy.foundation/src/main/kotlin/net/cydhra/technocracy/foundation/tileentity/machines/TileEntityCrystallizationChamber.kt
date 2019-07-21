package net.cydhra.technocracy.foundation.tileentity.machines

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.capabilities.inventory.DynamicInventoryHandler
import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.cydhra.technocracy.foundation.tileentity.components.InventoryComponent
import net.cydhra.technocracy.foundation.tileentity.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.tileentity.management.TEInventoryProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityCrystallizationChamber : MachineTileEntity(), TEInventoryProvider {

    /**
     * Input inventory for the furnace with one slot
     */
    private val inputFluidComponent = FluidComponent(capacity = 4000,
            allowedFluid = *arrayOf(),
            tanktype = DynamicFluidHandler.TankType.INPUT,
            facing = mutableSetOf(EnumFacing.WEST))

    /**
     * Output inventory for the furnace with one slot
     */
    private val outputInventoryComponent = InventoryComponent(1, this, EnumFacing.EAST)

    /**
     * All recipes of the pulverizer; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getRecipesByType(RecipeManager.RecipeType.CRYSTALLIZATION) ?: emptyList())
    }

    init {
        this.registerComponent(inputFluidComponent, "input_fluid")
        this.registerComponent(outputInventoryComponent, "output_inventory")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.CRYSTALLIZATION,
                inputFluidSlots = arrayOf(this.inputFluidComponent.fluid),
                outputInventory = this.outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                machineUpgrades = this.machineUpgradesComponent,
                baseTickEnergyCost = 60,
                progress = this.progressComponent))
    }

    override fun isItemValid(inventory: DynamicInventoryHandler, slot: Int, stack: ItemStack): Boolean {
        return inventory == outputInventoryComponent.inventory && this.recipes.any { it.getOutput()[0].item == stack.item }
    }
}