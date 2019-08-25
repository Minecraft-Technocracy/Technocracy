package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.blocks.general.chemicalReactionChamberBlock
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.TCCategoryUid
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class ChemicalReactionChamberRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<ChemicalReactionChamberRecipeCategory.ChemicalReactionChamberRecipeWrapper>(
        guiHelper,
        chemicalReactionChamberBlock,
        RecipeManager.RecipeType.CHEMICAL_REACTION,
        ChemicalReactionChamberRecipeWrapper::class.java,
        TCCategoryUid.CHEMICAL_REACTION
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(23, 38, Orientation.RIGHT, null, null).getDrawable(100)

    override fun getTitle(): String = "Chemical Reaction Chamber"

    override fun setRecipe(layout: IRecipeLayout, wrapper: ChemicalReactionChamberRecipeWrapper, ingredients: IIngredients) {
        val fluidStacks = layout.fluidStacks

        fluidStacks.init(0, true, 10, 10, 10, 50, 1000, false, fluidInputOverlay)
        fluidStacks.init(1, false, 48, 10, 10, 50, 1000, false, fluidOutputOverlay)

        fluidStacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressbarDrawable.draw(minecraft)
    }

    class ChemicalReactionChamberRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}