package net.cydhra.technocracy.foundation.integration.jei.gui

import mezz.jei.api.gui.IAdvancedGuiHandler
import net.cydhra.technocracy.foundation.client.gui.TCGui
import java.awt.Rectangle

object TCGuiHandler : IAdvancedGuiHandler<TCGui> {

    override fun getGuiContainerClass(): Class<TCGui> = TCGui::class.java

    override fun getGuiExtraAreas(guiContainer: TCGui): MutableList<Rectangle> {
        return mutableListOf(Rectangle(
                guiContainer.guiLeft + guiContainer.getTabBarPositionRelativeX(),
                guiContainer.guiTop + guiContainer.getTabBarPositionRelativeY(),
                guiContainer.getTabBarWidth(),
                guiContainer.getTabBarHeight()))
    }
}