package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.redstonemode.DefaultRedstoneModeControl
import net.cydhra.technocracy.foundation.content.tileentities.components.RedstoneModeTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.client.Minecraft
import net.minecraft.init.Items
import net.minecraft.util.ResourceLocation

class MachineSettingsTab(parent: TCGui, val machine: MachineTileEntity) : TCTab("Settings", parent,
        icon = TCIcon(Items.REDSTONE)) {

    override fun init() {
        var index = 0
        machine.getComponents().forEach {
            if (it.second is RedstoneModeTileEntityComponent) {
                val control = DefaultRedstoneModeControl(97, 20, it.second as RedstoneModeTileEntityComponent, parent)
                control.componentId = index++
                components.add(control)
            }
        }
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(x, y, mouseX, mouseY, partialTicks)
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Redstone Mode: ", 10f + x, 24f + y, -1)
    }

}