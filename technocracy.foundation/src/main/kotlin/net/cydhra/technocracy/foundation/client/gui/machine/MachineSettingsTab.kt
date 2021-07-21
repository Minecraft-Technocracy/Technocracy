package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.redstonemode.DefaultRedstoneModeControl
import net.cydhra.technocracy.foundation.content.tileentities.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityRedstoneModeComponent
import net.minecraft.client.Minecraft
import net.minecraft.init.Items

class MachineSettingsTab(parent: TCGui, val machine: MachineTileEntity) : TCTab("Settings", parent,
        icon = TCIcon(Items.REDSTONE)) {

    override fun init() {
        machine.getComponents().forEach {
            if (it.second is TileEntityRedstoneModeComponent) {
                val control = DefaultRedstoneModeControl(97, 20, it.second as TileEntityRedstoneModeComponent, parent)
                components.add(control)
            }
        }
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(x, y, mouseX, mouseY, partialTicks)
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Redstone Mode: ", 10f + x, 24f + y, -1)
    }

}