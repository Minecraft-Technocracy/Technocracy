package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

/**
 * A recipe data model for recipes that converts multiple [FluidStack]s into one single output [FluidStack]
 *
 * @param inputFluids multiple input [FluidStack]s
 * @param outputFluid output [FluidStack]
 * @param processingCost amount of processing the machine has to solve for this recipe
 */
class MFTFRecipe(
        val inputFluids: List<FluidStack>,
        val outputFluid: FluidStack,
        override val processingCost: Int) : IMachineRecipe {

    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return fluids.size == inputFluids.size && this.inputFluids.all { input -> fluids.any { it.isFluidEqual(input) } }
    }

    override fun getFluidInput(): List<FluidStack> {
        return this.inputFluids
    }

    override fun getFluidOutput(): List<FluidStack> {
        return listOf(this.outputFluid)
    }
}