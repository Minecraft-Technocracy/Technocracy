package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.blocks.general.polymerizationChamberBlock
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.TCCategoryUid
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class PolymerizationChamberRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<PolymerizationChamberRecipeCategory.PolymerizationChamberRecipeWrapper>(
        guiHelper,
        polymerizationChamberBlock,
        RecipeManager.RecipeType.ALLOY, //TODO change
        PolymerizationChamberRecipeWrapper::class.java,
        TCCategoryUid.POLYMERIZATION
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(44, 38, Orientation.RIGHT, null, null).getDrawable(100)

    override fun getTitle(): String = "Polymerization Chamber"

    override fun setRecipe(layout: IRecipeLayout, wrapper: PolymerizationChamberRecipeWrapper, ingredients: IIngredients) {
        val fluidStacks = layout.fluidStacks
        val itemStacks = layout.itemStacks

        fluidStacks.set(ingredients)
        itemStacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        //progressbarDrawable.draw(minecraft)
    }

    class PolymerizationChamberRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}