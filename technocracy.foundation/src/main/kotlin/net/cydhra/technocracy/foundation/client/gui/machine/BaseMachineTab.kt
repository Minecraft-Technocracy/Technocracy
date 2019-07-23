package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.tabs.TCTab
import net.cydhra.technocracy.foundation.network.MachineInfoRequest
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

abstract class BaseMachineTab(val machine: MachineTileEntity, parent: TCGui, icon: ResourceLocation) : TCTab(name = machine.blockType.localizedName, parent = parent, icon = icon) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().fontRenderer.drawString(machine.blockType.localizedName, 8f, 8f, -1, true)
        super.draw(mouseX, mouseY, partialTicks)
    }

    override fun update() {
        PacketHandler.sendToServer(MachineInfoRequest(parent.player.world.provider.dimension, machine.pos))
        super.update()
    }

}
