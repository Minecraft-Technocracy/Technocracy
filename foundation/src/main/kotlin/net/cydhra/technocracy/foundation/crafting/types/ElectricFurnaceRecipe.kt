package net.cydhra.technocracy.foundation.crafting.types

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

/**
 * A recipe model type for electric furnace recipes. One input, one output.
 */
data class ElectricFurnaceRecipe(val input: Ingredient, val output: ItemStack, override val processingCost: Int) : IRecipe {
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