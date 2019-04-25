package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

/**
 * A recipe model type for pulverizer recipe with one input type, one output type and processing time cost
 *
 * @param input the input item stack for the pulverizer
 * @param output the output produced with given input as an item stack
 * @param processingCost the base cost in ticks for the recipe to complete
 */
data class PulverizerRecipe(val input: Ingredient, val output: ItemStack, override val processingCost: Int) : IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>): Boolean {
        return stacks.size == 1 && this.input.test(stacks[0])
    }

    override fun getInput(): List<Ingredient> {
        return listOf(input)
    }

    override fun getOutput(): List<ItemStack> {
        return listOf(output)
    }
}