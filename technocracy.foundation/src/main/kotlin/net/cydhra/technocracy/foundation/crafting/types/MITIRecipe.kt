package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
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
class MITIRecipe(
        val inputItems: List<Ingredient>,
        val outputItem: ItemStack,
        override val processingCost: Int) : IMachineRecipe {

    override fun conforms(stacks: List<ItemStack>, fluids: List<FluidStack>): Boolean {
        return stacks.size == inputItems.size && stacks.all { stack -> inputItems.any { it.test(stack) } }
    }

    override fun getInput(): List<Ingredient> {
        return inputItems
    }

    override fun getOutput(): List<ItemStack> {
        return listOf(outputItem)
    }
}