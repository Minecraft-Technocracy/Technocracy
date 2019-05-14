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
data class ChemicalProcessingRecipe(val inputIngrdient: Ingredient, val inputFluid: FluidStack, val output: ItemStack,
                                    override val processingCost: Int) : IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return stacks.size == 1 && fluids.size == 1 && inputIngrdient.test(stacks[0])
                && inputFluid.isFluidEqual(fluids[0])
    }

    override fun getInput(): List<Ingredient> {
        return listOf(inputIngrdient)
    }

    override fun getOutput(): List<ItemStack> {
        return listOf(output)
    }

    override fun getFluidInput(): List<FluidStack> {
        return listOf(inputFluid)
    }
}