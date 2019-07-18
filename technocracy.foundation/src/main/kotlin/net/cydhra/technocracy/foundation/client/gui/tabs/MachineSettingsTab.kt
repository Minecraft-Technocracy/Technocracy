package net.cydhra.technocracy.foundation.client.gui.tabs

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.RedstoneModeComponent
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

class MachineSettingsTab(parent: TCGui, val machine: MachineTileEntity, val player: EntityPlayer) : TCTab("Settings", parent, icon = ResourceLocation("technocracy.foundation",
        "textures/item/gear.png")) {

    override fun init() {
        machine.getComponents().forEach {
            if(it.second is RedstoneModeComponent) {

            }
        }
    }

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(mouseX, mouseY, partialTicks)

        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Settings", 8f, 8f, -1)
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Redstone Mode: ", 10f, 24f, -1)
    }

    override fun update() {
        super.update()
    }
}