package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.redstonemode.DefaultRedstoneModeControl
import net.cydhra.technocracy.foundation.content.tileentities.components.RedstoneModeTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

class MachineSettingsTab(parent: TCGui, val machine: MachineTileEntity) : TCTab("Settings", parent,
        icon = ResourceLocation("minecraft", "textures/items/redstone_dust.png")) {

    override fun init() {
        machine.getComponents().forEach {
            if (it.second is RedstoneModeTileEntityComponent) {
                components.add(DefaultRedstoneModeControl(97, 20, it.second as RedstoneModeTileEntityComponent, parent))
            }
        }
    }

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(mouseX, mouseY, partialTicks)
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Redstone Mode: ", 10f, 24f, -1)
    }

}