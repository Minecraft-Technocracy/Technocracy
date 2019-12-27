package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlot
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.inventory.Slot
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.config.GuiUtils

open class TCGui(val guiWidth: Int = 176, val guiHeight: Int = 166, val container:
TCContainer)
    : GuiContainer(container) {

    data class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int)

    companion object {
        val guiComponents: ResourceLocation = ResourceLocation("technocracy.foundation", "textures/gui/components.png")

        const val windowBodyColor = 0xFFC6C6C6.toInt()
        const val inactiveTabTint = 0xFF505050.toInt()

        const val TAB_WIDTH = 25
        const val TAB_HEIGHT = 25

        const val TAB_GAP_WIDTH = 3

        const val TAB_SELECTED_WIDTH = TAB_WIDTH + TAB_GAP_WIDTH
        const val TAB_SELECTED_HEIGHT = TAB_HEIGHT + TAB_GAP_WIDTH

        val left = Rectangle(0, 4, 4, 1)
        val right = Rectangle(6, 3, 4, 1)
        val top = Rectangle(4, 0, 1, 4)
        val bottom = Rectangle(4, 6, 1, 4)
        val cornerTopLeft = Rectangle(0, 0, 4, 4)
        val cornerTopRight = Rectangle(7, 0, 3, 3)
        val cornerBottomLeft = Rectangle(0, 7, 3, 3)
        val cornerBottomRight = Rectangle(6, 6, 4, 4)
    }

    val tabs: ArrayList<TCTab> = ArrayList()
    var guiX: Int = 0
    var guiY: Int = 0

    private var activeTabIndex: Int = 0

    init {
        this.xSize = guiWidth
        this.ySize = guiHeight
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        guiX = (width - xSize) / 2
        guiY = (height - ySize) / 2

        GlStateManager.pushMatrix()
        GlStateManager.translate(guiX.toDouble(), guiY.toDouble(), 0.0)

        drawWindow(0.0, 0.0, xSize, ySize)

        if (tabs.isNotEmpty()) {
            drawTabs(partialTicks, mouseX, mouseY)
            this.tabs[this.activeTabIndex].draw(mouseX - guiX, mouseY - guiY, partialTicks)
        }

        GlStateManager.popMatrix()
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY) // draws f.e. items in slots
        this.tabs[activeTabIndex].drawToolTips(mouseX - guiX, mouseY - guiY)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        tabs[activeTabIndex].mouseClicked(mouseX - guiX, mouseY - guiY, mouseButton)

        this.tabs.indices.filterNot { it == this.activeTabIndex }.forEach {
            val x = getTabBarPositionRelativeX() + TAB_GAP_WIDTH
            val y = it * TAB_SELECTED_HEIGHT + getTabBarPositionRelativeY() + TAB_GAP_WIDTH
            val width = TAB_WIDTH
            val height = TAB_HEIGHT

            if (this.isPointInRegion(x, y, width, height, mouseX, mouseY)) {
                this.activeTabIndex = it

                this.tabs.withIndex().forEach { (index, tab) ->
                    tab.components.filterIsInstance<Slot>().map { index to it }.forEach { pair ->
                        if (pair.second is TCSlot) {
                            (pair.second as TCSlot).isEnabled = pair.first == it
                        } else if (pair.second is TCSlotIO) {
                            (pair.second as TCSlotIO).isEnabled = pair.first == it
                        }
                    }
                }
            }
        }
    }

    private fun drawTabs(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.tabs.withIndex().filterNot { it.index == this.activeTabIndex }.forEach { (i, tab) ->
            val x = getTabBarPositionRelativeX().toDouble() + TAB_GAP_WIDTH
            val y = (i * TAB_SELECTED_HEIGHT).toDouble() + getTabBarPositionRelativeY() + TAB_GAP_WIDTH
            val width = TAB_WIDTH
            val height = TAB_HEIGHT

            drawWindow(x, y, width, height, tab.tint and inactiveTabTint, true)

            if (tab.icon != null) {
                GlStateManager.pushMatrix()
                GlStateManager.translate(x + (width - 16) / 2, y + (height - 16) / 2 + 2, 0.0)
                Minecraft.getMinecraft().textureManager.bindTexture(tab.icon)
                GlStateManager.color(1F, 1F, 1F, 1F)
                drawModalRectWithCustomSizedTexture(0, 0, 0F, 0F, 17, 17, 17F, 17F)
                GlStateManager.popMatrix()
            }

        }

        val activeTab: TCTab = this.tabs[this.activeTabIndex]
        val activeTabX = getTabBarPositionRelativeX().toDouble()
        val activeTabY = (this.activeTabIndex * TAB_SELECTED_HEIGHT).toDouble() + getTabBarPositionRelativeY()
        val tabWidth = TAB_SELECTED_WIDTH
        val tabHeight = TAB_SELECTED_HEIGHT

        drawWindow(activeTabX, activeTabY, tabWidth, tabHeight, activeTab.tint and -1, true)

        if (activeTab.icon != null) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(activeTabX + (tabWidth - 16) / 2, activeTabY + (tabHeight - 16) / 2 + 1, 0.0)
            Minecraft.getMinecraft().textureManager.bindTexture(activeTab.icon)
            GlStateManager.color(1F, 1F, 1F, 1F)
            drawModalRectWithCustomSizedTexture(0, 0, 0F, 0F, 17, 17, 17F, 17F)
            GlStateManager.popMatrix()
        }

        tabs.withIndex().forEach { (i, tab) ->
            val x = getTabBarPositionRelativeX().toDouble()
            val y = (i * TAB_SELECTED_HEIGHT).toDouble() + TAB_GAP_WIDTH + getTabBarPositionRelativeY()
            val width = TAB_WIDTH
            val height = TAB_HEIGHT
            if (mouseX - guiX > x && mouseX - guiX < x + width && mouseY - guiY > y && mouseY - guiY < y + height) {
                renderTooltip(mutableListOf(tab.name), mouseX - guiX + 10, mouseY - guiY)
            }
        }
    }

    fun drawWindow(x: Double, y: Double, width: Int, height: Int, tint: Int = -1, windowAttachment: Boolean = false) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, 0.0)

        Gui.drawRect(if (windowAttachment) 0 else 4, 4, width, height, windowBodyColor and tint)

        GlStateManager.color((tint shr 8 and 255).toFloat() / 255.0F, (tint and 255).toFloat() / 255.0F,
                (tint shr 16 and 255).toFloat() / 255.0F, (tint shr 24 and 255).toFloat() / 255.0F)

        Minecraft.getMinecraft().textureManager.bindTexture(guiComponents)

        for (i in 3 until height) {
            if (!windowAttachment) {
                drawTexturedModalRect(0, i, left.x, left.y, left.width, left.height)
            }

            drawTexturedModalRect(width - 1, i, right.x, right.y, right.width, right.height)
        }

        for (i in (if (windowAttachment) 0 else 3) until width) {
            drawTexturedModalRect(i, 0, top.x, top.y, top.width, top.height)
            drawTexturedModalRect(i, height - 1, bottom.x, bottom.y, bottom.width, bottom.height)
        }

        if (!windowAttachment) {
            drawTexturedModalRect(0, 0, cornerTopLeft.x, cornerTopLeft.y, cornerTopLeft.width, cornerTopLeft.height)
            drawTexturedModalRect(0, height, cornerBottomLeft.x, cornerBottomLeft.y, cornerBottomLeft.width, cornerBottomLeft.height)
        }

        drawTexturedModalRect(width, 0, cornerTopRight.x, cornerTopRight.y, cornerTopRight.width, cornerTopRight.height)
        drawTexturedModalRect(width - 1, height - 1, cornerBottomRight.x, cornerBottomRight.y, cornerBottomRight.width,
                cornerBottomRight.height)
        GlStateManager.popMatrix()
    }

    override fun updateScreen() {
        this.tabs[this.activeTabIndex].update()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    fun registerTab(tab: TCTab) {
        this.tabs.add(tab)
        tab.init()

        tab.components.forEach { this.container.registerComponent(it) }
    }

    fun unregisterTab(tab: TCTab) {
        this.tabs.remove(tab)
    }

    fun renderHoveredItemToolTip(mouseX: Int, mouseY: Int) {
        super.renderHoveredToolTip(mouseX, mouseY)
    }

    fun renderTooltip(_str: MutableList<String>, mouseX: Int, mouseY: Int) { // have to modify the one of forge, because forge makes it unusable
        if (_str.isNotEmpty()) {
            var str = _str
            val sr = ScaledResolution(Minecraft.getMinecraft())
            val screenWidth = sr.scaledWidth
            val screenHeight = sr.scaledHeight
            val maxTextWidth = 100
            val font = Minecraft.getMinecraft().fontRenderer

            GlStateManager.disableRescaleNormal()
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableLighting()
            GlStateManager.disableDepth()
            var tooltipTextWidth = 0

            for (textLine in str) {
                val textLineWidth = font.getStringWidth(textLine)

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth
                }
            }

            var needsWrap = false

            var titleLinesCount = 1
            var tooltipX = mouseX + 12
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth
                if (tooltipX < 4) {
                    tooltipTextWidth = if (mouseX > screenWidth / 2) {
                        mouseX - 12 - 8
                    } else {
                        screenWidth - 16 - mouseX
                    }
                    needsWrap = true
                }
            }

            if (maxTextWidth in 1 until tooltipTextWidth) {
                tooltipTextWidth = maxTextWidth
                needsWrap = true
            }

            if (needsWrap) {
                var wrappedTooltipWidth = 0
                val wrappedTextLines = mutableListOf<String>()
                for (i in str.indices) {
                    val textLine = str[i]
                    val wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth)
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size
                    }

                    for (line in wrappedLine) {
                        val lineWidth = font.getStringWidth(line)
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth
                        }
                        wrappedTextLines.add(line)
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth
                str = wrappedTextLines

                tooltipX = if (mouseX > screenWidth / 2) {
                    mouseX - 16 - tooltipTextWidth
                } else {
                    mouseX + 12
                }
            }

            var tooltipY = mouseY - 12
            var tooltipHeight = 8

            if (str.size > 1) {
                tooltipHeight += (str.size - 1) * 10
                if (str.size > titleLinesCount) {
                    tooltipHeight += 2
                }
            }

            if (tooltipY < 4) {
                tooltipY = 4
            } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 4
            }

            val zLevel = 300
            val backgroundColor = -0xfeffff0
            val borderColorStart = 0x505000FF
            val borderColorEnd = borderColorStart and 0xFEFEFE shr 1 or (borderColorStart and -0x1000000)
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor)
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd)
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd)
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart)
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd)

            for (lineNumber in str.indices) {
                val line = str[lineNumber]
                font.drawStringWithShadow(line, tooltipX.toFloat(), tooltipY.toFloat(), -1)

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2
                }

                tooltipY += 10
            }

            // GlStateManager.enableLighting() // should stay disabled
            GlStateManager.enableDepth()
            //RenderHelper.enableStandardItemLighting() // should stay disabled
            GlStateManager.enableRescaleNormal()
        }
    }

    override fun onGuiClosed() {
        tabs[activeTabIndex].onClose()
    }

    /**
     * Get the position of the tab bar relative to the gui window position
     */
    fun getTabBarPositionRelativeX(): Int {
        return this.xSize
    }

    /**
     * Get the position of the tab bar relative to the gui window position
     */
    fun getTabBarPositionRelativeY(): Int {
        return 0
    }

    /**
     * Get the width of the tab bar
     */
    fun getTabBarWidth(): Int {
        return TAB_SELECTED_WIDTH
    }

    /**
     * Get the height of the tab bar
     */
    fun getTabBarHeight(): Int {
        return this.guiHeight - getTabBarPositionRelativeY()
    }
}
