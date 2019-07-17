package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

data class CrystallizationRecipe(val inputFluid: FluidStack, val output: ItemStack,
                                 override val processingCost: Int) : IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return stacks.isEmpty() && fluids.size == 1 && inputFluid.isFluidEqual(fluids[0])
    }

    override fun getOutput(): List<ItemStack> {
        return listOf(output)
    }

    override fun getFluidInput(): List<FluidStack> {
        return listOf(inputFluid)
    }
}