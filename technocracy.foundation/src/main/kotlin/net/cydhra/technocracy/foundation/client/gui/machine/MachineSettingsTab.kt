package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.redstonemode.DefaultRedstoneModeControl
import net.cydhra.technocracy.foundation.client.gui.tabs.TCTab
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
                components.add(DefaultRedstoneModeControl(97, 20, it.second as RedstoneModeComponent, parent))
            }
        }
    }

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(mouseX, mouseY, partialTicks)

        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Settings", 8f, 8f, -1)
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Redstone Mode: ", 10f, 24f, -1)
    }

}