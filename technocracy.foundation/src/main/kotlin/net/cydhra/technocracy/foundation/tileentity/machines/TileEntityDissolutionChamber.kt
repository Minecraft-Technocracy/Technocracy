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
class TileEntityDissolutionChamber : MachineTileEntity(), TEInventoryProvider {

    private val inputInventoryComponent = InventoryComponent(1, this, EnumFacing.WEST)
    private val inputFluidComponent = FluidComponent(4000,
            tanktype = DynamicFluidHandler.TankType.INPUT, facing = mutableSetOf(EnumFacing.UP))
    private val outputFluidComponent = FluidComponent(4000,
            tanktype = DynamicFluidHandler.TankType.OUTPUT, facing = mutableSetOf(EnumFacing.EAST))

    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getRecipesByType(RecipeManager.RecipeType.DISSOLUTION) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(inputFluidComponent, "input_fluid")
        this.registerComponent(outputFluidComponent, "output_fluid")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.DISSOLUTION,
                inputInventory = this.inputInventoryComponent.inventory,
                inputFluidSlots = arrayOf(inputFluidComponent.fluid),
                outputFluidSlots = arrayOf(outputFluidComponent.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                machineUpgrades = this.machineUpgradesComponent,
                baseTickEnergyCost = 40,
                progress = this.progressComponent
        ))
    }

    override fun isItemValid(inventory: DynamicInventoryHandler, slot: Int, stack: ItemStack): Boolean {
        return inventory == inputInventoryComponent.inventory && this.recipes.any { recipe ->
            recipe.getInput().any { it.test(stack) }
        }
    }
}