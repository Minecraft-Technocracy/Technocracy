package net.cydhra.technocracy.foundation.tileentity.logic

import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorage
import net.cydhra.technocracy.foundation.capabilities.inventory.DynamicInventoryHandler
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.crafting.types.IRecipe
import net.cydhra.technocracy.foundation.tileentity.components.MachineUpgradesComponents
import net.minecraft.item.ItemStack

class ItemProcessingLogic(private val recipeType: RecipeManager.RecipeType,
                          private val inputInventory: DynamicInventoryHandler,
                          private val outputInventory: DynamicInventoryHandler,
                          private val energyStorage: DynamicEnergyStorage,
                          private val machineUpgrades: MachineUpgradesComponents,
                          private val baseTickEnergyCost: Int) : ILogic {

    companion object {
        // TODO this could be a value obtained from config
        const val baseMachineProgressPerTick = 1
    }

    /**
     * All recipes of the machine type this logic handles; loaded lazily so they are not loaded before the first
     * [update] tick, as they might not have been registered yet.
     */
    private val recipes: Collection<IRecipe> by lazy {
        (RecipeManager.getRecipesByType(this.recipeType) ?: emptyList())
    }

    /**
     * Currently processed recipe
     */
    private var currentRecipe: IRecipe? = null

    /**
     * Current progress of recipe processing.
     */
    private var processingProgress: Int = 0

    override fun update() {
        // collect input item stacks
        val input = (0 until inputInventory.slots).map(inputInventory::getStackInSlot)

        assert(recipes.filter { it.conforms(input) }.size <= 1)
        val activeRecipe = recipes.firstOrNull { it.conforms(input) }

        // check if active recipe is still the same as last tick
        if (this.currentRecipe != activeRecipe) {
            // update current recipe and reset progress
            this.processingProgress = 0
            this.currentRecipe = activeRecipe
        }

        // process recipe
        if (this.currentRecipe != null) {
            // if more progress is required then try to add it
            if (this.processingProgress < this.currentRecipe!!.processingCost) {
                if (energyStorage.consumeEnergy(this.getTickEnergyCost())) {
                    this.processingProgress += this.getTickProgressAmount()
                }
            }

            // if enough progress happened, try process the recipe (if enough space for recipe output is present)
            if (this.processingProgress >= this.currentRecipe!!.processingCost) {
                val recipeOutput = this.currentRecipe!!.getOutput()
                assert(recipeOutput.size <= this.outputInventory.slots)

                // check if the output fits into the output slots
                if (recipeOutput.zip(0 until this.outputInventory.slots).all { (outputStack, outputSlot) ->
                            this.outputInventory.insertItem(outputSlot, outputStack, true) == ItemStack.EMPTY
                        }) {
                    // consume input items
                    val recipeInputRequirements = this.currentRecipe!!.getInput()
                    recipeInputRequirements.forEach { ingredient ->
                        for (slot in (0 until this.inputInventory.slots)) {
                            if (ingredient.test(inputInventory.getStackInSlot(slot))) {
                                inputInventory.extractItem(slot, 1, false)
                                break
                            }
                        }
                    }

                    // insert output items
                    recipeOutput.zip(0 until this.outputInventory.slots).forEach { (outputStack, outputSlot) ->
                        this.outputInventory.insertItem(outputSlot, outputStack, false)
                    }

                    // reset progress and the machine is good to go
                    this.processingProgress = 0
                }
            }

        }
    }

    /**
     * Get the energy cost of progressing one tick. This is the base cost multiplied with all related upgrades.
     */
    fun getTickEnergyCost(): Int {
        // TODO upgrade modifier calculation
        return this.baseTickEnergyCost
    }

    /**
     * Get the progress to recipes the machine does per tick. This is the base progress (1 by default) multiplied
     * with related upgrade multipliers
     */
    fun getTickProgressAmount(): Int {
        return baseMachineProgressPerTick
    }

}