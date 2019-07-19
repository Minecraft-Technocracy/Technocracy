package net.cydhra.technocracy.foundation.client.gui.tabs

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.machine.MachineContainer
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

abstract class BaseMachineTab(machine: MachineTileEntity, parent: TCGui, icon: ResourceLocation) : TCTab(name = machine.blockType.localizedName, parent = parent, icon = icon) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString((parent.container as MachineContainer).machine.blockType.localizedName, 8f, 8f, -1, true)
        super.draw(mouseX, mouseY, partialTicks)
    }

}
