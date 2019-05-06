package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.FluidStack

/**
 * A recipe model type for alloy smeltery recipes. Two or three inputs, one output.
 */
data class AlloySmelteryRecipe(val inputIngredients: List<Ingredient>, val output: ItemStack,
                               override val processingCost: Int) : IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return stacks.size == inputIngredients.size &&
                inputIngredients.all { ingredient -> stacks.any { ingredient.test(it) } }
    }

    override fun getInput(): List<Ingredient> {
        return inputIngredients
    }

    override fun getOutput(): List<ItemStack> {
        return listOf(output)
    }
}