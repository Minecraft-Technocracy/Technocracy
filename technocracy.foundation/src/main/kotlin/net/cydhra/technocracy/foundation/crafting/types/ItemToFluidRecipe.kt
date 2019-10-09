package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

/**
 * A recipe data model for recipes that convert one single [Ingredient] into one single output [FluidStack]
 *
 * @param inputItem single input [Ingredient]
 * @param outputFluid output [FluidStack]
 * @param processingCost amount of processing the machine has to solve for this recipe
 */
class ItemToFluidRecipe(
        val inputItem: Ingredient,
        val outputFluid: FluidStack,
        override val processingCost: Int) : IMachineRecipe {

    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return stacks.size == 1 && this.inputItem.test(stacks[0])
    }

    override fun getInput(): List<Ingredient> {
        return listOf(inputItem)
    }

    override fun getFluidOutput(): List<FluidStack> {
        return listOf(this.outputFluid)
    }
}