package net.cydhra.technocracy.foundation.integration.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import net.cydhra.technocracy.foundation.integration.jei.machines.*
import net.minecraft.item.ItemStack

@JEIPlugin
class TechnocracyPlugin : IModPlugin {

    private lateinit var categories: List<AbstractRecipeCategory<*>> // must use lateinit because construction of category requires IGuiHandler instance, which is only available in jei methods

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        val guiHelper = registry.jeiHelpers.guiHelper

        categories = listOf<AbstractRecipeCategory<*>>(
                AlloyRecipeCategory(guiHelper),
                CentrifugeRecipeCategory(guiHelper),
                CompactorRecipeCategory(guiHelper),
                ElectricFurnaceRecipeCategory(guiHelper),
                PulverizerRecipeCategory(guiHelper),
                ElectrolysisChamberRecipeCategory(guiHelper)
        )

        categories.forEach { category ->
            registry.addRecipeCategories(category)
        }
    }

    override fun register(registry: IModRegistry) {
        categories.forEach { category ->
            registry.addRecipes(TCRecipeMaker().getRecipes(category.recipeType, category.recipeWrapperClass), category.categoryUid)
            registry.addRecipeCatalyst(ItemStack(category.displayBlock), category.categoryUid)
        }
    }
}