package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.blocks.general.chemicalEtchingChamberBlock
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.TCCategoryUid
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class ChemicalEtchingChamberRecipeCategory(guiHelper: IGuiHelper) : AbstractRecipeCategory<ChemicalEtchingChamberRecipeCategory.ChemicalEtchingChamberRecipeWrapper>(
        guiHelper,
        chemicalEtchingChamberBlock,
        RecipeManager.RecipeType.CHEMICAL_ETCHING,
        ChemicalEtchingChamberRecipeWrapper::class.java,
        TCCategoryUid.CHEMICAL_ETCHING
) {

    private val progressbarDrawable: IDrawable = DefaultProgressBar(44, 38, Orientation.RIGHT, null, null).getDrawable(100)

    override fun getTitle(): String = "Chemical Etching Chamber"

    override fun setRecipe(layout: IRecipeLayout, wrapper: ChemicalEtchingChamberRecipeWrapper, ingredients: IIngredients) {
        val fluidStacks = layout.fluidStacks
        val itemStacks = layout.itemStacks



        fluidStacks.set(ingredients)
        itemStacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        progressbarDrawable.draw(minecraft)
    }

    class ChemicalEtchingChamberRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}