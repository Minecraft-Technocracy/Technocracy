package net.cydhra.technocracy.foundation.client.gui.components.redstonemode

import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.content.tileentities.components.RedstoneModeTileEntityComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.foundation.network.componentsync.ComponentUpdatePacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

class DefaultRedstoneModeControl(posX: Int, posY: Int, val component: RedstoneModeTileEntityComponent, val gui: TCGui) : RedstoneModeControl(posX, posY) {

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(x, y, mouseX, mouseY, partialTicks)
        val clr = if (hovered) 0.7f else 1f
        GlStateManager.color(clr, clr, clr, 1f)

        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        Gui.drawModalRectWithCustomSizedTexture(posX + x, posY + y, component.redstoneMode.ordinal * 16f, 59f, width, height, 256f, 256f)
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val str = "Redstone Mode: ${component.redstoneMode.name}"
        gui.drawHoveringText(mutableListOf(str), mouseX, mouseY)
    }

    override fun handleClientClick(player: EntityPlayer, mouseButton: Int) {
        if (mouseButton == 0) {
            component.redstoneMode = RedstoneModeTileEntityComponent.RedstoneMode.values()[(component.redstoneMode.ordinal + 1) % RedstoneModeTileEntityComponent.RedstoneMode.values().size]
        }
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        component.redstoneMode = RedstoneModeTileEntityComponent.RedstoneMode.values()[(component.redstoneMode.ordinal + 1) % RedstoneModeTileEntityComponent.RedstoneMode.values().size]
        super.mouseClicked(x, y, mouseX, mouseY, mouseButton)
    }
}