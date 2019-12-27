package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
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
class TileEntityCompactor : MachineTileEntity(), TEInventoryProvider {

    /**
     * Input inventory for the furnace with one slot
     */
    private val inputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.WEST)

    /**
     * Output inventory for the furnace with one slot
     */
    private val outputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.EAST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    private val upgradesComponent = MachineUpgradesTileEntityComponent(3,
            setOf(MACHINE_UPGRADE_ENERGY, MACHINE_UPGRADE_SPEED, MACHINE_UPGRADE_GENERIC),
            setOf(MachineUpgradeClass.MECHANICAL, MachineUpgradeClass.ELECTRICAL, MachineUpgradeClass.ALIEN),
            setOf(this.processingSpeedComponent, this.energyCostComponent))

    /**
     * All recipes of the pulverizer; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.COMPACTOR) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(outputInventoryComponent, "output_inventory")
        this.registerComponent(upgradesComponent, "upgrades")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.COMPACTOR,
                inputInventory = this.inputInventoryComponent.inventory,
                outputInventory = this.outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 10,
                progress = this.progressComponent))
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return (inventory == inputInventoryComponent.inventory && this.recipes.any { it.getInput()[0].test(stack) }) ||
                (inventory == outputInventoryComponent.inventory && this.recipes
                        .any { it.getOutput()[0].item == stack.item })
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }
}