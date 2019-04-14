package net.cydhra.technocracy.foundation.crafting.types

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

interface IRecipe {

    /**
     * The tick cost a machine must pay to process the recipe
     */
    val processingCost: Int

    /**
     * Match a set of input stacks against the recipe. If the input stacks conform in type and size to the recipe,
     * true is returned.
     *
     * @param stacks all input stacks that shall be matched against the recipe
     *
     * @return if the recipe can be successfully processed from the input stacks
     */
    fun conforms(stacks: List<ItemStack>): Boolean

    /**
     * Get a list of ingredients that represent the recipe input.
     */
    fun getInput(): List<Ingredient>

    /**
     * Get all output of the recipe in a list sorted by the output stacks of the machine. For machines that have
     * different stacks of output, this sorting is important and the processing logic assumes, that it is sorted by
     * ascending output inventory slot id.
     */
    fun getOutput(): List<ItemStack>
}