package net.cydhra.technocracy.foundation.data.crafting.types

import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.FluidStack

/**
 * A recipe data model for recipes that convert multiple [Ingredient]s into one single output [ItemStack]
 *
 * @param inputItems input [Ingredient]s
 * @param outputItem output [ItemStack]
 * @param processingCost amount of processing the machine has to solve for this recipe
 */
class MIMFTIRecipe(
        val inputItems: List<Ingredient>,
        val inputFluids: List<FluidStack>,
        val outputItem: ItemStack,
        override val processingCost: Int) : IMachineRecipe {

    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return stacks.size == inputItems.size && stacks.all { stack -> inputItems.any { it.test(stack) } } &&
                fluids.size == inputFluids.size &&
                this.inputFluids.all { input -> fluids.any { it.isFluidEqual(input) } }
    }

    override fun getInput(): List<Ingredient> {
        return inputItems
    }

    override fun getFluidInput(): List<FluidStack> {
        return inputFluids
    }

    override fun getOutput(): List<ItemStack> {
        return listOf(outputItem)
    }
}