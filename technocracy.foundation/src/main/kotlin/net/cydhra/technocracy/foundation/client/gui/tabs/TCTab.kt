package net.cydhra.technocracy.foundation.client.gui.tabs

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.client.gui.components.TCSlot
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.util.ResourceLocation

abstract class TCTab(val parent: TCGui, val tint: Int = -1,
                     val icon: ResourceLocation? =
                             ResourceLocation("technocracy.foundation", "textures/item/gear.png")) {


    val components: ArrayList<TCComponent> = ArrayList()

    abstract fun init()

    open fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.components.forEach {
            if (it is Slot) {
                val slot = it as Slot

                Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
                GlStateManager.color(1F, 1F, 1F, 1F)
                GuiContainer.drawModalRectWithCustomSizedTexture(slot.xPos - 1, slot.yPos - 1, 0F, 10F, 18, 18, 256F,
                        256F)
            }
        }
    }

    open fun update() {

    }

    protected fun addPlayerInventorySlots(player: EntityPlayer, x: Int, y: Int) {
        for (row in 0..2) {
            for (slot in 0..8) {
                this.components.add(TCSlot(player.inventory, slot + row * 9 + 9,
                        x + slot * 18, y + row * 18))
            }
        }

        for (k in 0..8) {
            this.components.add(TCSlot(player.inventory, k, x + k * 18, y + 58))
        }
    }

}
