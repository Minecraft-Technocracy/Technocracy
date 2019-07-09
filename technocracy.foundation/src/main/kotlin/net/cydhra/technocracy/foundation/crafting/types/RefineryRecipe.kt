package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

/**
 * A recipe model type for the oil refinery with one input, and two outputs
 *
 * @param input the input item stacks for the etching chamber
 * @param output the output produced with given input as an item stack
 * @param processingCost the base cost in ticks for the recipe to complete
 */
data class RefineryRecipe(val inputFluid: FluidStack, val outputs: List<FluidStack>, override val processingCost: Int) : IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return fluids.size == 1 && inputFluid.fluid == fluids[0].fluid
    }

    override fun getFluidInput(): List<FluidStack> {
        return listOf(inputFluid)
    }

    override fun getFluidOutput(): List<FluidStack> {
        return outputs
    }
}