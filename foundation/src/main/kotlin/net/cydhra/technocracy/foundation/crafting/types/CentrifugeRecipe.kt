package net.cydhra.technocracy.foundation.crafting.types

import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

/**
 * A recipe model type for centrifuge recipe with one input type, two output types of which is one optional and
 * processing time cost
 *
 * @param input the input item stack for the pulverizer
 * @param output the output produced with given input as an item stack
 * @param processingCost the base cost in ticks for the recipe to complete
 */
data class CentrifugeRecipe(val input: Ingredient, val output: ItemStack, val secondaryOutput: ItemStack?,
                            override val processingCost: Int) :
        IMachineRecipe {
    override fun conforms(stacks: List<ItemStack>): Boolean {
        return stacks.size == 1 && this.input.test(stacks[0])
    }

    override fun getInput(): List<Ingredient> {
        return listOf(input)
    }

    override fun getOutput(): List<ItemStack> {
        return listOfNotNull(output, secondaryOutput)
    }
}