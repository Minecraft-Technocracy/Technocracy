package net.cydhra.technocracy.foundation.integration.jei.multiblocks

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.content.blocks.refineryControllerBlock
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
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

    private val progressbarDrawable: IDrawable = DefaultProgressBar(23, 38, Orientation.RIGHT, null, object: TCGui {
        override val origWidth: Int
            get() = TODO("Not yet implemented")
        override val origHeight: Int
            get() = TODO("Not yet implemented")
        override var guiWidth: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        override var guiHeight: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        override var guiX: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        override var guiY: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        override val container: TCContainer
            get() = TODO("Not yet implemented")

        override fun setActiveTab(index: Int) {
            TODO("Not yet implemented")
        }

        override fun getActiveTab(): TCTab {
            TODO("Not yet implemented")
        }

        override fun getTabs(): List<TCTab> {
            TODO("Not yet implemented")
        }

        override fun registerTab(tab: TCTab) {
            TODO("Not yet implemented")
        }

        override fun getTab(index: Int): TCTab {
            TODO("Not yet implemented")
        }
    }).getDrawable(100, guiHelper)

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