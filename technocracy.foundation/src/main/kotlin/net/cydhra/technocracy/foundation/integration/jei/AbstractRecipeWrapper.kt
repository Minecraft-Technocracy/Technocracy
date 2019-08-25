package net.cydhra.technocracy.foundation.integration.jei

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

abstract class AbstractRecipeWrapper(private val inputStacks: List<List<ItemStack>>, private val outputStacks: List<ItemStack>, private val inputFluidStacks: List<FluidStack>, private val outputFluidStacks: List<FluidStack>) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, inputStacks)
        ingredients.setOutputs(VanillaTypes.ITEM, outputStacks)

        ingredients.setInputs(VanillaTypes.FLUID, inputFluidStacks)
        ingredients.setOutputs(VanillaTypes.FLUID, outputFluidStacks)
    }
}