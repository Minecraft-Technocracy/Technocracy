package net.cydhra.technocracy.foundation.client.gui.tabs

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.ProgressBar
import net.cydhra.technocracy.foundation.client.gui.machine.MachineContainer
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

/**
 * just a tab for me to test stuff. [WipTab] is probably no longer needed?
 */
class DemoTab(parent: TCGui, val machine: MachineTileEntity, val player: EntityPlayer) : TCTab("Machine Tab", parent, icon = ResourceLocation("technocracy.foundation",
        "textures/item/silicon.png")) {

    override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(mouseX, mouseY, partialTicks)

        val machine = (parent.container as MachineContainer).machine
        val str = machine.blockType.localizedName
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(str, 8F, 8F, -1)
    }

    override fun init() {
        addPlayerInventorySlots(player, 8, 84)
    }
}
