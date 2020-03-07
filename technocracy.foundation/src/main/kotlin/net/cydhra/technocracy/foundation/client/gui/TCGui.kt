package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.components.slot.ITCSlot
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.foundation.network.componentsync.ClientRequestSyncPacket
import net.cydhra.technocracy.foundation.network.componentsync.ComponentUpdatePacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.config.GuiUtils
import org.lwjgl.opengl.GL11
import kotlin.math.max

open class TCGui(guiWidth: Int = 176, guiHeight: Int = 166, val container:
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

        val slotCornerTopLeft = Rectangle(0, 10, 2, 2)
        val slotCornerTopRight = Rectangle(16, 10, 2, 2)
        val slotCornerBottomLeft = Rectangle(0, 26, 2, 2)
        val slotCornerBottomRight = Rectangle(16, 26, 2, 2)

        val slotLineTop = Rectangle(1, 10, 16, 1)
        val slotLineBottom = Rectangle(1, 27, 16, 1)
        val slotLineLeft = Rectangle(0, 11, 1, 16)
        val slotLineRight = Rectangle(17, 11, 1, 16)

        val slotContent = Rectangle(1, 11, 15, 15)
    }

    val tabs: ArrayList<TCTab> = ArrayList()
    var guiX: Int = 0
    var guiY: Int = 0

    var guiWidth: Int
        get() {
            return this.xSize
        }
        set(value) {
            this.xSize = value
        }

    var guiHeight: Int
        get() {
            return this.ySize
        }
        set(value) {
            this.ySize = value
        }

    val origWidth: Int
    val origHeight: Int

    private var activeTabIndex: Int = 0

    init {
        this.xSize = guiWidth
        this.ySize = guiHeight
        origWidth = guiWidth
        origHeight = guiHeight
    }

    override fun initGui() {
        super.initGui()
        PacketHandler.sendToServer(ClientRequestSyncPacket())
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {

        val partialTicks = Minecraft.getMinecraft().renderPartialTicks

        drawDefaultBackground()

        xSize = this.tabs[this.activeTabIndex].getSizeX()
        ySize = this.tabs[this.activeTabIndex].getSizeY()

        guiX = (width - xSize) / 2
        guiY = (height - ySize) / 2

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

        //draw gui background
        drawWindow(guiX, guiY, xSize, ySize)

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

        if (tabs.size > 1) {
            drawTabs()
        }

        //render tab content
        this.tabs[this.activeTabIndex].draw(guiX, guiY, mouseX, mouseY, partialTicks)

        //drawTooltips(mouseX, mouseY)

        super.drawScreen(mouseX, mouseY, partialTicks)

    }

    fun drawTooltips(mouseX: Int, mouseY: Int) {
        if (tabs.size > 1) {
            tabs.withIndex().filterNot { it.index == activeTabIndex }.forEach { (i, tab) ->
                val x = getTabBarPositionRelativeX() + TAB_GAP_WIDTH
                val y = i * TAB_SELECTED_HEIGHT + getTabBarPositionRelativeY() + TAB_GAP_WIDTH

                if (mouseX > x && mouseX < x + TAB_WIDTH && mouseY > y && mouseY < y + TAB_HEIGHT) {
                    drawHoveringText(mutableListOf(tab.name), mouseX, mouseY)
                }
            }

            val activeTabX = getTabBarPositionRelativeX()
            val activeTabY = activeTabIndex * TAB_SELECTED_HEIGHT + getTabBarPositionRelativeY()

            if (mouseX > activeTabX && mouseX < activeTabX + TAB_SELECTED_WIDTH && mouseY > activeTabY && mouseY < activeTabY + TAB_SELECTED_HEIGHT) {
                drawHoveringText(mutableListOf(tabs[activeTabIndex].name), mouseX, mouseY)
            }
        }

        this.tabs[activeTabIndex].drawToolTips(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {}

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        this.zLevel = 200f
        //remove the translation that is added by vanilla
        drawTooltips(mouseX - guiX, mouseY - guiY)
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        this.zLevel = 0f
    }

    override fun handleMouseInput() {
        this.tabs[this.activeTabIndex].handleMouseInput()
        super.handleMouseInput()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        var active = tabs[activeTabIndex]
        active.mouseClicked(guiX, guiY, mouseX, mouseY, mouseButton)

        if (tabs.size > 1) {
            for (index in tabs.indices) {
                if (index == activeTabIndex) continue
                val x = getTabBarPositionRelativeX() + TAB_GAP_WIDTH + guiX
                val y = index * TAB_SELECTED_HEIGHT + getTabBarPositionRelativeY() + TAB_GAP_WIDTH + guiY
                // check if this tab has been clicked
                if (mouseX > x && mouseX < x + TAB_WIDTH && mouseY > y && mouseY < y + TAB_HEIGHT) {
                    activeTabIndex = index
                    active = tabs[activeTabIndex]

                    //update state of components
                    tabs.forEachIndexed { inner, tcTab ->
                        tcTab.components.filterIsInstance<ITCSlot>().forEach {
                            it.setEnabled(inner == index)
                        }
                    }

                    //update position of the components to new gui size
                    xSize = active.getSizeX()
                    ySize = active.getSizeY()
                    guiX = (width - xSize) / 2
                    guiY = (height - ySize) / 2
                    onResize(mc, width, height)

                    break
                }
            }
        }
    }

    private fun renderTabCard(x: Int, y: Int, width: Int, height: Int, active: Boolean, tab: TCTab) {
        drawWindow(x, y, width, height, tab.tint and if (active) -1 else inactiveTabTint, true)

        tab.icon.draw((x + (width - 16) / 2), (y + (height - 16) / 2 + 2))
    }

    private fun drawTabs() {
        tabs.withIndex().filterNot { it.index == activeTabIndex }.forEach { (i, tab) ->
            val x = getTabBarPositionRelativeX() + TAB_GAP_WIDTH + guiX
            val y = i * TAB_SELECTED_HEIGHT + getTabBarPositionRelativeY() + TAB_GAP_WIDTH + guiY
            renderTabCard(x, y, TAB_WIDTH, TAB_HEIGHT, false, tab)
        }

        val activeTabX = getTabBarPositionRelativeX() + guiX
        val activeTabY = activeTabIndex * TAB_SELECTED_HEIGHT + getTabBarPositionRelativeY() + guiY
        renderTabCard(activeTabX, activeTabY, TAB_SELECTED_WIDTH, TAB_SELECTED_HEIGHT, true, tabs[activeTabIndex])
    }

    fun drawWindow(x: Int, y: Int, width: Int, height: Int, tint: Int = -1, windowAttachment: Boolean = false) {
        Gui.drawRect((if (windowAttachment) 0 else 4) + x, 4 + y, width + x, height + y, windowBodyColor and tint)

        GlStateManager.color((tint shr 8 and 255).toFloat() / 255.0F, (tint and 255).toFloat() / 255.0F,
                (tint shr 16 and 255).toFloat() / 255.0F, (tint shr 24 and 255).toFloat() / 255.0F)

        Minecraft.getMinecraft().textureManager.bindTexture(guiComponents)

        for (i in 3 until height) {
            if (!windowAttachment) {
                drawTexturedModalRect(x, i + y, left.x, left.y, left.width, left.height)
            }

            drawTexturedModalRect(width - 1 + x, i + y, right.x, right.y, right.width, right.height)
        }

        for (i in (if (windowAttachment) 0 else 3) until width) {
            drawTexturedModalRect(i + x, y, top.x, top.y, top.width, top.height)
            drawTexturedModalRect(i + x, height - 1 + y, bottom.x, bottom.y, bottom.width, bottom.height)
        }

        if (!windowAttachment) {
            drawTexturedModalRect(x, y, cornerTopLeft.x, cornerTopLeft.y, cornerTopLeft.width, cornerTopLeft.height)
            drawTexturedModalRect(x,
                    height + y,
                    cornerBottomLeft.x,
                    cornerBottomLeft.y,
                    cornerBottomLeft.width,
                    cornerBottomLeft.height)
        }

        drawTexturedModalRect(width + x, y, cornerTopRight.x, cornerTopRight.y, cornerTopRight.width, cornerTopRight.height)
        drawTexturedModalRect(width - 1 + x, height - 1 + y, cornerBottomRight.x, cornerBottomRight.y, cornerBottomRight.width,
                cornerBottomRight.height)
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

        // disable slots of tabs that aren't the first one (which is the active one by default)
        if (this.tabs.size > 1) {
            tab.components.filterIsInstance<ITCSlot>().forEach { it.setEnabled(false) }
        }

    }

    fun unregisterTab(tab: TCTab) {
        this.tabs.remove(tab)
    }

    fun renderHoveredItemToolTip(mouseX: Int, mouseY: Int) {
        super.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun onResize(mcIn: Minecraft, w: Int, h: Int) {
        //set size of gui before a resize
        xSize = this.tabs[this.activeTabIndex].getSizeX()
        ySize = this.tabs[this.activeTabIndex].getSizeY()

        guiX = (width - xSize) / 2
        guiY = (height - ySize) / 2

        super.onResize(mcIn, w, h)
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
        return if(tabs.size > 1) TAB_SELECTED_WIDTH else 0
    }

    /**
     * Get the height of the tab bar
     */
    fun getTabBarHeight(): Int {
        return ((tabs.size * TAB_SELECTED_HEIGHT).toDouble() + getTabBarPositionRelativeY() + TAB_GAP_WIDTH).toInt()
    }
}
