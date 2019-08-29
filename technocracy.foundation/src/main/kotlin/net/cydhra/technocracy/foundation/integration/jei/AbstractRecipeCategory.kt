package net.cydhra.technocracy.foundation.integration.jei

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IDrawableStatic
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.minecraft.block.Block

abstract class AbstractRecipeCategory<T : IRecipeWrapper>(protected val guiHelper: IGuiHelper, val displayBlock: Block, val recipeType: RecipeManager.RecipeType, val recipeWrapperClass: Class<out AbstractRecipeWrapper>, val categoryUid: String) : IRecipeCategory<T> {

    protected val slotDrawable: IDrawableStatic = guiHelper.slotDrawable
    protected val fluidInputOverlay: IDrawable = guiHelper.drawableBuilder(TCGui.guiComponents, 10, 75, 10, 50).build()
    protected val fluidOutputOverlay: IDrawable = guiHelper.drawableBuilder(TCGui.guiComponents, 0, 75, 10, 50).build()

    override fun getUid(): String {
        return categoryUid
    }

    override fun getModName(): String {
        return TCFoundation.MODID
    }

    override fun getBackground(): IDrawable {
        return guiHelper.createBlankDrawable(165, 90)
    }

}