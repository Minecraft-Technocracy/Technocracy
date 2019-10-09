package net.cydhra.technocracy.foundation.integration.jei.multiblocks

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.content.blocks.refineryControllerBlock
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class RefineryRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<RefineryRecipeCategory.RefineryRecipeWrapper>(
        guiHelper,
        refineryControllerBlock,
        RecipeManager.RecipeType.REFINERY,
        RefineryRecipeWrapper::class.java,
        "${TCFoundation.MODID}.${RecipeManager.RecipeType.REFINERY.toString().toLowerCase()}"
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(23, 38, Orientation.RIGHT, null, null).getDrawable(100, guiHelper)

    override fun getTitle(): String = "Refinery"

    override fun setRecipe(layout: IRecipeLayout, wrapper: RefineryRecipeWrapper, ingredients: IIngredients) {
        val fluidStacks = layout.fluidStacks

        fluidStacks.init(0, true, 10, 10, 10, 50, 1000, false, fluidInputOverlay)
        fluidStacks.init(1, false, 48, 10, 10, 50, 1000, false, fluidOutputOverlay)
        fluidStacks.init(2, false, 61, 10, 10, 50, 1000, false, fluidOutputOverlay)

        fluidStacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressbarDrawable.draw(minecraft)
    }

    class RefineryRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}