package net.cydhra.technocracy.foundation.integration.jei

import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreIngredient

class TCRecipeMaker() {

    fun getRecipes(type: RecipeManager.RecipeType, wrapperClass: Class<out AbstractRecipeWrapper>): List<AbstractRecipeWrapper> {
        val recipes = mutableListOf<AbstractRecipeWrapper>()
        RecipeManager.getRecipesByType(type)?.forEach { recipe ->

            val inputStacks = mutableListOf<List<ItemStack>>()
            recipe.getInput().filterIsInstance<OreIngredient>().forEach { ingredient ->
                val oreDictInputs = mutableListOf<ItemStack>()
                oreDictInputs.addAll(ingredient.matchingStacks)
                inputStacks.add(oreDictInputs)
            }

            recipes.add(wrapperClass.getDeclaredConstructor(List::class.java, List::class.java).newInstance(inputStacks, recipe.getOutput()) as AbstractRecipeWrapper) // if something is wrong with the constructor, an exception should be thrown

        }
        return recipes
    }


}