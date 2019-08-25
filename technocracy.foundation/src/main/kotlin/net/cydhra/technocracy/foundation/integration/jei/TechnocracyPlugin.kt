package net.cydhra.technocracy.foundation.integration.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import net.cydhra.technocracy.foundation.integration.jei.machines.*
import net.cydhra.technocracy.foundation.integration.jei.multiblocks.RefineryRecipeCategory
import net.minecraft.item.ItemStack

@JEIPlugin
class TechnocracyPlugin : IModPlugin {

    private lateinit var categories: List<AbstractRecipeCategory<*>> // must use lateinit because construction of category requires IGuiHandler instance, which is only available in jei methods

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        val guiHelper = registry.jeiHelpers.guiHelper

        categories = listOf<AbstractRecipeCategory<*>>(
                AlloyRecipeCategory(guiHelper),
                CentrifugeRecipeCategory(guiHelper),
                ChemicalProcessingChamberRecipeCategory(guiHelper),
                ChemicalEtchingChamberRecipeCategory(guiHelper), // seems not finished yet
                ChemicalOxidizerRecipeCategory(guiHelper),
                ChemicalReactionChamberRecipeCategory(guiHelper),
                CompactorRecipeCategory(guiHelper),
                ElectricFurnaceRecipeCategory(guiHelper),
                ElectrolysisChamberRecipeCategory(guiHelper),
                PyrolysisKilnRecipeCategory(guiHelper),
                //PolymerizationChamberRecipeCategory(guiHelper), // seems not finished yet
                PulverizerRecipeCategory(guiHelper),
                DissolutionChamberRecipeCategory(guiHelper),
                CrystallizationChamberRecipeCategory(guiHelper),

                RefineryRecipeCategory(guiHelper)
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