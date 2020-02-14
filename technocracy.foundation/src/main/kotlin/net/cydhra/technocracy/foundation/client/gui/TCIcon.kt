package net.cydhra.technocracy.foundation.client.gui

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation


class TCIcon {
    var location: ResourceLocation? = null
    var u: Float = -1f
    var v: Float = -1f
    var width: Int = -1
    var height: Int = -1
    var textureWidth: Float = 256f
    var textureHeight: Float = 256f

    var stack: ItemStack? = null

    constructor(location: ResourceLocation) {
        this.location = location
    }

    constructor(stack: ItemStack) {
        this.stack = stack
    }

    constructor(item: Item) {
        this.stack = ItemStack(item)
    }

    constructor(block: Block) {
        this.stack = ItemStack(block)
    }

    fun setDimensions(u: Float, v: Float, width: Int, height: Int, textureWidth: Float = 256f, textureHeight: Float = 256f): TCIcon {
        this.u = u
        this.v = v
        this.width = width
        this.height = height
        this.textureWidth = textureWidth
        this.textureHeight = textureHeight
        return this
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun draw(x: Int, y: Int) {
        if (location != null) {
            Minecraft.getMinecraft().textureManager.bindTexture(location)
            GlStateManager.color(1F, 1F, 1F, 1F)
            GuiContainer.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight)
        } else if (stack != null) {
            RenderHelper.enableGUIStandardItemLighting()
            Minecraft.getMinecraft().renderItem.renderItemIntoGUI(stack, x, y)
            RenderHelper.disableStandardItemLighting()
            GlStateManager.enableAlpha()
        }
    }

}