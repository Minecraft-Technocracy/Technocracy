package net.cydhra.technocracy.foundation.client.gui.tabs

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlot
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

abstract class TCTab(val name: String, val parent: TCGui, val tint: Int = -1,
                     val icon: ResourceLocation? =
                             ResourceLocation("technocracy.foundation", "textures/item/gear.png")) {


    val components: ArrayList<TCComponent> = ArrayList()

    abstract fun init()

    open fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString(name, 8f, 8f, -1, true)
        this.components.forEach {
            it.draw(mouseX, mouseY, partialTicks)
        }
    }

    open fun drawToolTips(mouseX: Int, mouseY: Int) {
        components.forEach {
            if(it.isMouseOnComponent(mouseX, mouseY))
                it.drawTooltip(mouseX, mouseY)
        }
    }

    open fun update() {
        this.components.forEach(TCComponent::update)
    }

    open fun onClose() {}

    open fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton:Int) {
        components.forEach {
            if(it.isMouseOnComponent(mouseX, mouseY))
                it.mouseClicked(mouseX, mouseY, mouseButton)
        }
    }

    protected fun addPlayerInventorySlots(player: EntityPlayer, x: Int, y: Int) {
        for (row in 0..2) {
            for (slot in 0..8) {
                this.components.add(TCSlot(player.inventory, slot + row * 9 + 9,
                        x + slot * 18, y + row * 18, parent))
            }
        }

        for (k in 0..8) {
            this.components.add(TCSlot(player.inventory, k, x + k * 18, y + 58, parent))
        }
    }

}
