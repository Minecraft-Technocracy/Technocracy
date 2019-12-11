package net.cydhra.technocracy.foundation.content.tileentities.logic

import net.cydhra.technocracy.foundation.content.capabilities.energy.DynamicEnergyCapability
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.MultiplierComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.ProgressComponent
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogic
import net.minecraft.item.ItemStack

class ItemProcessingLogic(private val recipeType: RecipeManager.RecipeType,
                          private val inputInventory: DynamicInventoryCapability? = null,
                          private val outputInventory: DynamicInventoryCapability? = null,
                          private val inputFluidSlots: Array<DynamicFluidCapability> = emptyArray(),
                          private val outputFluidSlots: Array<DynamicFluidCapability> = emptyArray(),
                          private val energyStorage: DynamicEnergyCapability,
                          private val processSpeedComponent: MultiplierComponent,
                          private val energyCostComponent: MultiplierComponent,
                          private val baseTickEnergyCost: Int,
                          private val progress: ProgressComponent) : ILogic {

    companion object {
        // TODO this could be a value obtained from config
        const val baseMachineProgressPerTick = 1
    }

    /**
     * All recipes of the machine type this logic handles; loaded lazily so they are not loaded before the first
     * [update] tick, as they might not have been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(this.recipeType) ?: emptyList())
    }

    /**
     * Currently processed recipe
     */
    private var currentRecipe: IMachineRecipe? = null

    /**
     * Current progress of recipe processing.
     */
    private var processingProgress: Int = 0

    override fun preProcessing(): Boolean {
        // collect input item stacks
        val inputItems = if (inputInventory != null) {
            (0 until inputInventory.slots).map(inputInventory::getStackInSlot).filter { !it.isEmpty }
        } else {
            emptyList()
        }

        // collect input fluid stacks
        val inputFluids = inputFluidSlots.mapNotNull { it.currentFluid }

        assert(recipes.filter { it.conforms(inputItems, inputFluids) }.size <= 1)
        val activeRecipe = recipes.firstOrNull { it.conforms(inputItems, inputFluids) }

        // check if active recipe is still the same as last tick
        if (this.currentRecipe != activeRecipe) {
            // update current recipe and reset progress
            this.processingProgress = 0
            this.currentRecipe = activeRecipe

            if (this.currentRecipe == null) {
                progress.progress = 0
            }
        }

        return this.currentRecipe != null && energyStorage.consumeEnergy(this.getTickEnergyCost(), simulate = true)
    }

    override fun processing() {
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
                val recipeFluidOutput = this.currentRecipe!!.getFluidOutput()
                assert(recipeOutput.size <= this.outputInventory?.slots ?: 0)
                assert(recipeFluidOutput.size <= this.outputFluidSlots.size)

                // check if the output fits into the output slots
                if (recipeOutput.zip(0 until (this.outputInventory?.slots ?: 0))
                                .all { (outputStack, outputSlot) ->
                                    this.outputInventory?.insertItem(outputSlot, outputStack, simulate = true, forced = true) == ItemStack.EMPTY
                                }
                        && recipeFluidOutput.zip(this.outputFluidSlots.indices)
                                .all { (fluidStack, fluidSlot) ->
                                    this.outputFluidSlots[fluidSlot].fill(fluidStack, false) == fluidStack.amount
                                }) {
                    // consume input items
                    val recipeInputRequirements = this.currentRecipe!!.getInput()
                    recipeInputRequirements.forEach { ingredient ->
                        for (slot in (0 until (this.inputInventory?.slots ?: 0))) {
                            if (ingredient.test(inputInventory?.getStackInSlot(slot))) {
                                inputInventory?.extractItem(slot, 1, simulate = false, forced = true)
                                break
                            }
                        }
                    }

                    // consume input fluids
                    val recipeFluidRequirements = this.currentRecipe!!.getFluidInput()
                    recipeFluidRequirements.forEach { ingredient ->
                        for (slot in this.inputFluidSlots.indices) {
                            if (ingredient.isFluidEqual(this.inputFluidSlots[slot].currentFluid)) {
                                inputFluidSlots[slot].drain(ingredient, true)
                            }
                        }
                    }

                    // insert output items
                    recipeOutput.zip(0 until (this.outputInventory?.slots ?: 0)).forEach { (outputStack, outputSlot) ->
                        this.outputInventory!!.insertItem(outputSlot, outputStack.copy(), simulate = false, forced = true)
                    }

                    // insert output fluids
                    recipeFluidOutput.zip(this.outputFluidSlots.indices).forEach { (fluidStack, outputSlot) ->
                        this.outputFluidSlots[outputSlot].fill(fluidStack, true)
                    }

                    // reset progress and the machine is good to go
                    this.processingProgress = 0
                }
            }
        }

        progress.progress = if (currentRecipe != null)
            ((processingProgress.toFloat() / currentRecipe!!.processingCost.toFloat()) * 100f).toInt() else 0
    }

    override fun postProcessing(wasProcessing: Boolean) {

    }

    /**
     * Get the energy cost of progressing one tick. This is the base cost multiplied with all related upgrades.
     */
    private fun getTickEnergyCost(): Int {
        // TODO upgrade modifier calculation
        return this.baseTickEnergyCost
    }

    /**
     * Get the progress to recipes the machine does per tick. This is the base progress (1 by default) multiplied
     * with related upgrade multipliers
     */
    private fun getTickProgressAmount(): Int {
        return baseMachineProgressPerTick
    }

}