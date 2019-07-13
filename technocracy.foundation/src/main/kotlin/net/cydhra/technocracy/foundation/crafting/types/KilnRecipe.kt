package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

/**
 */
data class KilnRecipe(val inputFluid: FluidStack, val output: FluidStack,
                      override val processingCost: Int) : IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return fluids.size == 1 && inputFluid.isFluidEqual(fluids[0])
    }

    override fun getFluidInput(): List<FluidStack> {
        return listOf(inputFluid)
    }

    override fun getFluidOutput(): List<FluidStack> {
        return listOf(output)
    }
}