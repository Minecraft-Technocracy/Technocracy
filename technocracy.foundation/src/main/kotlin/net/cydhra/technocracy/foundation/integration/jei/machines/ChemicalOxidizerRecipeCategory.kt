package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.blocks.general.chemicalOxidizerBlock
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.TCCategoryUid
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class ChemicalOxidizerRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<ChemicalOxidizerRecipeCategory.ChemicalOxidizerRecipeWrapper>(
        guiHelper,
        chemicalOxidizerBlock,
        RecipeManager.RecipeType.CHEMICAL_OXIDIZER,
        ChemicalOxidizerRecipeWrapper::class.java,
        TCCategoryUid.CHEMICAL_OXIDIZER
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(31, 38, Orientation.RIGHT, null, null).getDrawable(100, guiHelper)

    override fun getTitle(): String = "Chemical Oxidizer"

    override fun setRecipe(layout: IRecipeLayout, wrapper: ChemicalOxidizerRecipeWrapper, ingredients: IIngredients) {
        val fluidStacks = layout.fluidStacks
        val itemStacks = layout.itemStacks

        itemStacks.init(0, true, 10, 36)
        fluidStacks.init(0, false, 56, 10, 10, 50, 1000, false, fluidInputOverlay)

        fluidStacks.set(ingredients)
        itemStacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressbarDrawable.draw(minecraft)
    }

    class ChemicalOxidizerRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}