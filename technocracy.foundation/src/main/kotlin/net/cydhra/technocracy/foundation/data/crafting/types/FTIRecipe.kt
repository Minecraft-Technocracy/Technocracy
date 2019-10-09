package net.cydhra.technocracy.foundation.data.crafting.types

import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

/**
 * A recipe data model for recipes that convert one single [FluidStack] into one single output [ItemStack]
 *
 * @param inputFluid input [FluidStack]
 * @param outputStack output [ItemStack]
 * @param processingCost amount of processing the machine has to solve for this recipe
 */
class FTIRecipe(
        val inputFluid: FluidStack,
        val outputStack: ItemStack,
        override val processingCost: Int) : IMachineRecipe {

    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return fluids.size == 1 && this.inputFluid.isFluidEqual(inputFluid)
    }

    override fun getFluidInput(): List<FluidStack> {
        return listOf(this.inputFluid)
    }

    override fun getOutput(): List<ItemStack> {
        return listOf(outputStack)
    }
}