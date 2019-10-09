package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

/**
 * A recipe data model for recipes that convert one single [Ingredient] and a single [FluidStack] into one single output
 * [FluidStack]
 *
 * @param inputItem single input [Ingredient]
 * @param inputFluid single input [FluidStack]
 * @param outputFluid output [FluidStack]
 * @param processingCost amount of processing the machine has to solve for this recipe
 */
class IFTFRecipe(
        val inputItem: Ingredient,
        val inputFluid: FluidStack,
        val outputFluid: FluidStack,
        override val processingCost: Int) : IMachineRecipe {

    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return stacks.size == 1 && this.inputItem.test(stacks[0])
                && fluids.size == 1 && this.inputFluid.isFluidEqual(fluids[0])
    }

    override fun getInput(): List<Ingredient> {
        return listOf(inputItem)
    }

    override fun getFluidInput(): List<FluidStack> {
        return listOf(inputFluid)
    }

    override fun getFluidOutput(): List<FluidStack> {
        return listOf(outputFluid)
    }
}