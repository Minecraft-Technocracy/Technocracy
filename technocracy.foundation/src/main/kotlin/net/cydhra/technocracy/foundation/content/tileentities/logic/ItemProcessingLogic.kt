package net.cydhra.technocracy.foundation.content.tileentities.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicParameters
import net.cydhra.technocracy.foundation.content.capabilities.energy.DynamicEnergyCapability
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityMultiplierComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityProgressComponent
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.minecraft.item.ItemStack

class ItemProcessingLogic(private val recipeType: RecipeManager.RecipeType,
                          private val inputInventory: DynamicInventoryCapability? = null,
                          private val outputInventory: DynamicInventoryCapability? = null,
                          private val inputFluidSlots: Array<DynamicFluidCapability> = emptyArray(),
                          private val outputFluidSlots: Array<DynamicFluidCapability> = emptyArray(),
                          private val energyStorage: DynamicEnergyCapability,
                          private val processSpeedComponent: TileEntityMultiplierComponent,
                          private val energyCostComponent: TileEntityMultiplierComponent,
                          private val baseTickEnergyCost: Int,
                          private val progress: TileEntityProgressComponent) : ILogic<ILogicParameters> {

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
     * Current progress of recipe processing. Processing progress keeps going up until reaching the current recipes
     * processing cost value. However, progress is stored multiplied by 100, to be able to handle decimal speed ups.
     */
    private var processingProgress: Int = 0

    override fun preProcessing(logicParameters: ILogicParameters): Boolean {
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

        if (this.currentRecipe != null) {
            val recipeOutput = this.currentRecipe!!.getOutput()
            val recipeFluidOutput = this.currentRecipe!!.getFluidOutput()
            assert(recipeOutput.size <= this.outputInventory?.slots ?: 0)
            assert(recipeFluidOutput.size <= this.outputFluidSlots.size)

            // check if the output fits into the output slots
            if (recipeOutput.zip(0 until (this.outputInventory?.slots ?: 0))
                            .all { (outputStack, outputSlot) ->
                                this.outputInventory?.insertItem(outputSlot,
                                        outputStack,
                                        simulate = true,
                                        forced = true) == ItemStack.EMPTY
                            }
                    && recipeFluidOutput.zip(this.outputFluidSlots.indices)
                            .all { (fluidStack, fluidSlot) ->
                                this.outputFluidSlots[fluidSlot].fill(fluidStack, doFill = false, forced = true) == fluidStack.amount
                            }) {
                return energyStorage.consumeEnergy(this.getTickEnergyCost(), simulate = true)
            }
        }

        return false
    }

    override fun processing(logicParameters: ILogicParameters) {
        // process recipe
        if (this.currentRecipe != null) {
            // if more progress is required then try to add it
            // progress is stored multiplied with 100, so cost must as well
            if (this.processingProgress < this.currentRecipe!!.processingCost * 100) {
                if (energyStorage.consumeEnergy(this.getTickEnergyCost())) {
                    this.processingProgress += this.getTickProgressAmount()
                }
            }

            // if enough progress happened, try process the recipe (due to pre-processing-checks enough space in output
            // slots should be present)
            if (this.processingProgress >= this.currentRecipe!!.processingCost * 100) {
                val recipeOutput = this.currentRecipe!!.getOutput()
                val recipeFluidOutput = this.currentRecipe!!.getFluidOutput()

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
                            inputFluidSlots[slot].drain(ingredient, doDrain = true, forced = true)
                        }
                    }
                }

                // insert output items
                recipeOutput.zip(0 until (this.outputInventory?.slots ?: 0)).forEach { (outputStack, outputSlot) ->
                    this.outputInventory!!.insertItem(outputSlot, outputStack.copy(), simulate = false, forced = true)
                }

                // insert output fluids
                recipeFluidOutput.zip(this.outputFluidSlots.indices).forEach { (fluidStack, outputSlot) ->
                    this.outputFluidSlots[outputSlot].fill(fluidStack, doFill = true, forced = true)
                }

                // reset progress and the machine is good to go
                this.processingProgress = 0
            }
        }

        progress.progress = if (currentRecipe != null)
            (processingProgress.toFloat() / currentRecipe!!.processingCost.toFloat()).toInt() else 0
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ILogicParameters) {

    }

    /**
     * Get the energy cost of progressing one tick. This is the base cost multiplied with all related upgrades.
     */
    private fun getTickEnergyCost(): Int {
        // TODO upgrade modifier calculation
        return (this.baseTickEnergyCost * this.energyCostComponent.getCappedMultiplier()).toInt()
    }

    /**
     * Get the progress to recipes the machine does per tick. This is the base progress (1 by default) multiplied
     * with related upgrade multipliers
     */
    private fun getTickProgressAmount(): Int {
        return (100 * baseMachineProgressPerTick * this.processSpeedComponent.getCappedMultiplier()).toInt()
    }

}