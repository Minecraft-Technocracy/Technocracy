package net.cydhra.technocracy.foundation.data.crafting

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.FluidStack

interface IMachineRecipe : ISpecialRecipe {

    /**
     * The tick cost a machine must pay to process the recipe
     */
    val processingCost: Int

    /**
     * Match a set of input item stacks against the recipe. If the input stacks conform in type and size to the recipe,
     * true is returned.
     *
     * @param stacks all input stacks that shall be matched against the recipe
     *
     * @return if the recipe can be successfully processed from the input stacks
     */
    fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack> = emptyList()): Boolean

    /**
     * Get a list of ingredients that represent the recipe input.
     */
    fun getInput(): List<Ingredient> = emptyList()

    /**
     * Get a list of fluids that need to be put in for the recipe to work
     */
    fun getFluidInput(): List<FluidStack> = emptyList()

    /**
     * Get all output of the recipe in a list sorted by the output slots of the machine. For machines that have
     * different slots for output, this sorting is important and the processing logic assumes, that it is sorted by
     * ascending output inventory slot id.
     */
    fun getOutput(): List<ItemStack> = emptyList()

    /**
     * Get all fluid outputs of the recipe in a list sorted by the output slots of the machine. For machines that have
     * different slots for output, this sorting is important and the processing logic assumes, that it is sorted by
     * ascending output inventory slot id.
     */
    fun getFluidOutput(): List<FluidStack> = emptyList()
}