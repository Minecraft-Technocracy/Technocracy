package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.FluidStack

data class DissolutionRecipe(val inputIngredient: Ingredient, val inputFluid: FluidStack, val outputFluid: FluidStack,
                             override val processingCost: Int) : IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return stacks.size == 1 && fluids.size == 1 && inputIngredient.test(stacks[0])
                && inputFluid.isFluidEqual(fluids[0])
    }

    override fun getInput(): List<Ingredient> {
        return listOf(inputIngredient)
    }

    override fun getFluidInput(): List<FluidStack> {
        return listOf(inputFluid)
    }

    override fun getFluidOutput(): List<FluidStack> {
        return listOf(outputFluid)
    }
}