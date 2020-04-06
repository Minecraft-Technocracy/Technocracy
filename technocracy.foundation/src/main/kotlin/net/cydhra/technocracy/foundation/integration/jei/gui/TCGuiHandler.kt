package net.cydhra.technocracy.foundation.integration.jei.gui

import mezz.jei.api.gui.IAdvancedGuiHandler
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import java.awt.Rectangle

object TCGuiHandler : IAdvancedGuiHandler<TCClientGuiImpl> {

    override fun getGuiContainerClass(): Class<TCClientGuiImpl> = TCClientGuiImpl::class.java

    override fun getGuiExtraAreas(guiContainer: TCClientGuiImpl): MutableList<Rectangle> {
        return mutableListOf(Rectangle(
                guiContainer.guiLeft + guiContainer.getTabBarPositionRelativeX(),
                guiContainer.guiTop + guiContainer.getTabBarPositionRelativeY(),
                guiContainer.getTabBarWidth(),
                guiContainer.getTabBarHeight()))
    }
}