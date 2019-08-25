package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.blocks.general.electrolysisChamberBlock
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.TCCategoryUid
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class ElectrolysisChamberRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<ElectrolysisChamberRecipeCategory.ElectrolysisChamberRecipeWrapper>(
        guiHelper,
        electrolysisChamberBlock,
        RecipeManager.RecipeType.ELECTROLYSIS,
        ElectrolysisChamberRecipeWrapper::class.java,
        TCCategoryUid.ELECTROLYSIS
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(23, 38, Orientation.RIGHT, null, null).getDrawable(100)
    private val fluidInputOverlay: IDrawable = guiHelper.drawableBuilder(TCGui.guiComponents, 10, 75, 10, 50).build()
    private val fluidOutputOverlay: IDrawable = guiHelper.drawableBuilder(TCGui.guiComponents, 0, 75, 10, 50).build()

    override fun getTitle(): String = "Electrolysis Chamber"

    override fun setRecipe(layout: IRecipeLayout, wrapper: ElectrolysisChamberRecipeWrapper, ingredients: IIngredients) {
        val stacks = layout.fluidStacks

        stacks.init(0, true, 10, 10, 10, 50, 1000, false, fluidInputOverlay)
        stacks.init(1, false, 48, 10, 10, 50, 1000, false, fluidOutputOverlay)
        stacks.init(2, false, 61, 10, 10, 50, 1000, false, fluidOutputOverlay)


        stacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressbarDrawable.draw(minecraft)
    }

    class ElectrolysisChamberRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}