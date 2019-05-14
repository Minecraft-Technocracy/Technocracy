package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.FluidStack

/**
 * A recipe model type for an etching chamber recipe with a list of inputs, one output type and processing time cost
 *
 * @param input the input item stacks for the etching chamber
 * @param output the output produced with given input as an item stack
 * @param processingCost the base cost in ticks for the recipe to complete
 */
data class ChemicalReactionRecipe(val inputFluids: List<FluidStack>, val output: FluidStack,
                                  override val processingCost: Int) : IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return fluids.size == 2 && inputFluids.all { input -> fluids.any { input.isFluidEqual(it) } }
    }

    override fun getFluidInput(): List<FluidStack> {
        return inputFluids
    }

    override fun getFluidOutput(): List<FluidStack> {
        return listOf(output)
    }
}