package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.blocks.general.chemicalProcessingChamberBlock
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.TCCategoryUid
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class ChemicalProcessingChamberRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<ChemicalProcessingChamberRecipeCategory.ChemicalProcessingChamberRecipeWrapper>(
        guiHelper,
        chemicalProcessingChamberBlock,
        RecipeManager.RecipeType.CHEMICAL_PROCESSING,
        ChemicalProcessingChamberRecipeWrapper::class.java,
        TCCategoryUid.CHEMICAL_PROCESSING
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(44, 38, Orientation.RIGHT, null, null).getDrawable(100)

    override fun getTitle(): String = "Chemical Processing Chamber"

    override fun setRecipe(layout: IRecipeLayout, wrapper: ChemicalProcessingChamberRecipeWrapper, ingredients: IIngredients) {
        val fluidStacks = layout.fluidStacks
        val itemStacks = layout.itemStacks

        fluidStacks.init(0, true, 10, 10, 10, 50, 1000, false, fluidInputOverlay)
        itemStacks.init(0, true, 23, 36)
        itemStacks.init(1, false, 69, 36)

        fluidStacks.set(ingredients)
        itemStacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressbarDrawable.draw(minecraft)
    }

    class ChemicalProcessingChamberRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}