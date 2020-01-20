package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotPlayer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

abstract class TCTab(val name: String, val parent: TCGui, val tint: Int = -1,
                     val icon: ResourceLocation? =
                             ResourceLocation("technocracy.foundation", "textures/item/gear.png")) {


    val components: ArrayList<TCComponent> = ArrayList()

    abstract fun init()

    open fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString(name, 8f + x, 8f + y, -1, true)
        this.components.forEach {
            it.draw(x, y, mouseX, mouseY, partialTicks)
        }
    }

    open fun drawToolTips(x: Int, y: Int, mouseX: Int, mouseY: Int) {
        components.forEach {
            if (it.isMouseOnComponent(mouseX - x, mouseY - y))
                it.drawTooltip(mouseX - x, mouseY - y)
        }
    }

    open fun update() {
        this.components.forEach(TCComponent::update)
    }

    open fun onClose() {}

    open fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        components.forEach {
            if (it.isMouseOnComponent(mouseX - x, mouseY - y))
                it.mouseClicked(x, y, mouseX, mouseY, mouseButton)
        }
    }

    protected fun addPlayerInventorySlots(player: EntityPlayer, x: Int, y: Int) {
        for (row in 0..2) {
            for (slot in 0..8) {
                this.components.add(TCSlotPlayer(player.inventory, slot + row * 9 + 9,
                        x + slot * 18, y + row * 18, parent))
            }
        }

        for (k in 0..8) {
            this.components.add(TCSlotPlayer(player.inventory, k, x + k * 18, y + 58, parent))
        }
    }

    protected fun addComponent(component: TCComponent) {
        components.add(component)
    }

}
