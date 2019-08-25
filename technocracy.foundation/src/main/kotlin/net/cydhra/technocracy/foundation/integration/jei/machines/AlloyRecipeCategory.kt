package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.blocks.general.alloySmelteryBlock
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.TCCategoryUid
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class AlloyRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<AlloyRecipeCategory.AlloyRecipeWrapper>(
        guiHelper,
        alloySmelteryBlock,
        RecipeManager.RecipeType.ALLOY,
        AlloyRecipeWrapper::class.java,
        TCCategoryUid.ALLOY_SMELTING
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(67, 38, Orientation.RIGHT, null, null).getDrawable(100, guiHelper)

    override fun getTitle(): String = "Alloy Smeltery"

    override fun setRecipe(layout: IRecipeLayout, wrapper: AlloyRecipeWrapper, ingredients: IIngredients) {
        val stacks = layout.itemStacks
        stacks.init(0, true, 0, 36)
        stacks.init(1, true, 23, 36)
        stacks.init(2, true, 46, 36)
        stacks.init(3, false, 90, 36)

        stacks.setBackground(0, slotDrawable)
        stacks.setBackground(1, slotDrawable)
        stacks.setBackground(2, slotDrawable)
        stacks.setBackground(3, slotDrawable)

        stacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressbarDrawable.draw(minecraft)
    }

    class AlloyRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}