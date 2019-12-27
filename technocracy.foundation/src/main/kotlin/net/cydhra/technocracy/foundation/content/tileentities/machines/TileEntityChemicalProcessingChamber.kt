package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_ENERGY
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_SPEED
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityChemicalProcessingChamber : MachineTileEntity(), TEInventoryProvider {

    private val inputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.WEST)
    private val inputFluidComponent = FluidTileEntityComponent(4000,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.UP))
    private val outputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    private val upgradesComponent = MachineUpgradesTileEntityComponent(3,
            setOf(MACHINE_UPGRADE_ENERGY, MACHINE_UPGRADE_SPEED, MACHINE_UPGRADE_GENERIC),
            setOf(MachineUpgradeClass.CHEMICAL, MachineUpgradeClass.ALIEN),
            setOf(this.processingSpeedComponent, this.energyCostComponent))

    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.CHEMICAL_PROCESSING) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(inputFluidComponent, "input_fluid")
        this.registerComponent(outputInventoryComponent, "output_inventory")
        this.registerComponent(upgradesComponent, "upgrades")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.CHEMICAL_PROCESSING,
                inputInventory = this.inputInventoryComponent.inventory,
                inputFluidSlots = arrayOf(inputFluidComponent.fluid),
                outputInventory = outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 60,
                progress = this.progressComponent
        ))
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return inventory == inputInventoryComponent.inventory && this.recipes.any { recipe ->
            recipe.getInput().any { it.test(stack) }
        }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }
}