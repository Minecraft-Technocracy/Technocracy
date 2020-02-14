package net.cydhra.technocracy.foundation.client.gui.components.redstonemode

import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.content.tileentities.components.RedstoneModeTileEntityComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.foundation.network.componentsync.ClientComponentUpdatePacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import org.lwjgl.opengl.GL11

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

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        component.redstoneMode = RedstoneModeTileEntityComponent.RedstoneMode.values()[(component.redstoneMode.ordinal + 1) % RedstoneModeTileEntityComponent.RedstoneMode.values().size]
        val tag = NBTTagCompound()
        tag.setTag("component", component.serializeNBT())
        if (component.tile is MultiblockTileEntityBase) { // not tested yet for multiblocks (because multiblocks currently haven't redstonemode stuff)
            tag.setString("name", ((component.tile as MultiblockTileEntityBase).multiblockController as BaseMultiBlock).getComponents().filter { it.second == component }[0].first)
        } else if (component.tile is TCAggregatable) {
            tag.setString("name", (component.tile as TCAggregatable).getComponents().filter { it.second == component }[0].first)
        }
        tag.setLong("pos", component.tile.pos.toLong())
        PacketHandler.sendToServer(ClientComponentUpdatePacket(tag))
    }
}