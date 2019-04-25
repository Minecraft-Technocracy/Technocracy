package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.FluidStack

data class ChemicalOxidizerRecipe(val input: Ingredient, val output: FluidStack, override val processingCost: Int) :
        IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return stacks.size == 1 && input.test(stacks[0])
    }

    override fun getInput(): List<Ingredient> {
        return listOf(input)
    }

    override fun getFluidOutput(): List<FluidStack> {
        return listOf(output)
    }
}