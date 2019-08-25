package net.cydhra.technocracy.foundation.integration.jei

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.item.ItemStack

abstract class AbstractRecipeWrapper(private val inputStacks: List<List<ItemStack>>, private val outputStacks: List<ItemStack>) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, inputStacks)
        ingredients.setOutputs(VanillaTypes.ITEM, outputStacks)
    }
}