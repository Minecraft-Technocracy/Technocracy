package net.cydhra.technocracy.foundation.integration.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.machines.MachineRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.multiblocks.RefineryRecipeCategory
import net.minecraft.item.ItemStack

@JEIPlugin
class TechnocracyPlugin : IModPlugin {

    private val categories = mutableListOf<AbstractRecipeCategory<*>>()

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        val guiHelper = registry.jeiHelpers.guiHelper

        // automatically add categories for machines
        RecipeManager.RecipeType.values().forEach { recipeType ->
            if (recipeType.machineBlock != null && recipeType.tileEntityClass != null) {
                val category = MachineRecipeCategory(guiHelper, recipeType.tileEntityClass.getDeclaredConstructor().newInstance(), recipeType, "${TCFoundation.MODID}.${recipeType.toString().toLowerCase()}", recipeType.machineBlock)
                categories.add(category)
            }
        }

        // manually add categories for multiblocks
        categories.add(RefineryRecipeCategory(guiHelper))

        categories.forEach { category ->
            registry.addRecipeCategories(category)
        }
    }

    override fun register(registry: IModRegistry) {
        categories.forEach { category ->
            registry.addRecipes(loadRecipes(category.recipeType, category.recipeWrapperClass), category.categoryUid)
            registry.addRecipeCatalyst(ItemStack(category.displayBlock), category.categoryUid)
        }
    }

    private fun loadRecipes(type: RecipeManager.RecipeType, wrapperClass: Class<out AbstractRecipeWrapper>): List<AbstractRecipeWrapper> {
        val recipes = mutableListOf<AbstractRecipeWrapper>()
        RecipeManager.getMachineRecipesByType(type)?.forEach { recipe ->

            val inputStacks = mutableListOf<List<ItemStack>>()
            recipe.getInput().forEach { ingredient ->
                val oreDictInputs = mutableListOf<ItemStack>()
                oreDictInputs.addAll(ingredient.matchingStacks)
                inputStacks.add(oreDictInputs)
            }

            recipes.add(wrapperClass.getDeclaredConstructor(List::class.java, List::class.java, List::class.java, List::class.java)
                    .newInstance(inputStacks, recipe.getOutput(), recipe.getFluidInput(), recipe.getFluidOutput()) as AbstractRecipeWrapper)
        }
        return recipes
    }

}