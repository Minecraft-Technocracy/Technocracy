package net.cydhra.technocracy.foundation.client.gui.tabs

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.machine.MachineContainer
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

abstract class BaseMachineTab(machine: MachineTileEntity, parent: TCGui, icon: ResourceLocation) : TCTab(name = machine.javaClass.simpleName, parent = parent, icon = icon) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(mouseX, mouseY, partialTicks)
        Minecraft.getMinecraft().fontRenderer.drawString((parent.container as MachineContainer).machine.blockType.localizedName, 8f, 8f, -1, true)
    }

}
