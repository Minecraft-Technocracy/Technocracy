package net.cydhra.technocracy.foundation.client.gui.components.redstonemode

import net.cydhra.technocracy.foundation.api.ecs.IAggregatable
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityRedstoneModeComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.relauncher.Side

class DefaultRedstoneModeControl(posX: Int, posY: Int, val component: TileEntityRedstoneModeComponent, override var gui: TCGui) : RedstoneModeControl(posX, posY) {

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.draw(x, y, mouseX, mouseY, partialTicks)
        val clr = if (hovered) 0.7f else 1f
        GlStateManager.color(clr, clr, clr, 1f)

        Minecraft.getMinecraft().textureManager.bindTexture(TCClientGuiImpl.guiComponents)
        Gui.drawModalRectWithCustomSizedTexture(posX + x, posY + y, component.redstoneMode.ordinal * 16f, 59f, width, height, 256f, 256f)
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val str = "Redstone Mode: ${component.redstoneMode.name}"
        (gui as TCClientGuiImpl).drawHoveringText(mutableListOf(str), mouseX, mouseY)
    }

    override var onClick: ((Side, EntityPlayer, IAggregatable?, Int) -> Unit)? = { side, player, tileEntity, button ->
        component.redstoneMode = TileEntityRedstoneModeComponent.RedstoneMode.values()[(component.redstoneMode.ordinal + 1) % TileEntityRedstoneModeComponent.RedstoneMode.values().size]
    }

    /*override fun handleClientClick(player: EntityPlayer, mouseButton: Int) {
        if (mouseButton == 0) {
            component.redstoneMode = RedstoneModeTileEntityComponent.RedstoneMode.values()[(component.redstoneMode.ordinal + 1) % RedstoneModeTileEntityComponent.RedstoneMode.values().size]
        }
    }*/

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(x, y, mouseX, mouseY, mouseButton)
    }
}