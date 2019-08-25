package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.blocks.general.compactorBlock
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.TCCategoryUid
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class CompactorRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<CompactorRecipeCategory.CompactorRecipeWrapper>(
        guiHelper,
        compactorBlock,
        RecipeManager.RecipeType.COMPACTOR,
        CompactorRecipeWrapper::class.java,
        TCCategoryUid.COMPACTOR
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(21, 38, Orientation.RIGHT, null, null).getDrawable(100)

    override fun setRecipe(layout: IRecipeLayout, wrapper: CompactorRecipeWrapper, ingredients: IIngredients) {
        val stacks = layout.itemStacks
        stacks.init(0, true, 0, 36)
        stacks.init(1, false, 44, 36)

        stacks.setBackground(0, slotDrawable)
        stacks.setBackground(1, slotDrawable)

        stacks.set(ingredients)
    }

    override fun getTitle(): String {
        return "Compactor"
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressbarDrawable.draw(minecraft)
    }

    class CompactorRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}