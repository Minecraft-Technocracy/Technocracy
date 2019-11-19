package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.label.DefaultLabel
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 * A tile entity linked to a pulverizer block that can store up to two stacks of items and processes the first stack
 * into its output (second) stack, if the processing output and second stack can be merged.
 */
class TileEntityAlloySmeltery : MachineTileEntity(), TEInventoryProvider {

    /**
     * Input inventory for the pulverizer with one slot
     */
    private val inputInventoryComponent = InventoryComponent(3, this, EnumFacing.WEST)

    /**
     * Output inventory for the pulverizer with one slot
     */
    private val outputInventoryComponent = InventoryComponent(1, this, EnumFacing.EAST)

    /**
     * All recipes of the pulverizer; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.ALLOY) ?: emptyList())
    }

    init {
        this.registerComponent(inputInventoryComponent, "input_inventory")
        this.registerComponent(outputInventoryComponent, "output_inventory")

        this.addLogicStrategy(ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.ALLOY,
                inputInventory = this.inputInventoryComponent.inventory,
                outputInventory = this.outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                machineUpgrades = this.machineUpgradesComponent,
                baseTickEnergyCost = 10,
                progress = this.progressComponent))
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return inventory == inputInventoryComponent.inventory && this.recipes.any { recipe ->
            recipe.getInput().any { it.test(stack) }
        }
                && (0 until this.inputInventoryComponent.inventory.slots).all { index ->
            index == slot || !this.inputInventoryComponent.inventory.getStackInSlot(index).isItemEqual(stack)
        }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack) {
    }

    override fun initGui(gui: TCGui) {
        gui.registerTab(object: TCTab("Example", gui) {
            override fun init() {
                addComponent(DefaultLabel(10, 20, "Hello World"))
            }
        })
    }
}