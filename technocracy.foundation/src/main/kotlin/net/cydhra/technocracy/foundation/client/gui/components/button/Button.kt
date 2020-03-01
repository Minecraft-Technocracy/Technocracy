package net.cydhra.technocracy.foundation.client.gui.components.button

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.entity.player.EntityPlayer
import java.util.function.Consumer


abstract class Button(val posX: Int, val posY: Int, val width: Int, val height: Int, val text: String, val fontRenderer: FontRenderer, val onClick: Consumer<EntityPlayer>) : TCComponent() {

    override fun drawTooltip(mouseX: Int, mouseY: Int) {}

    override fun update() {}

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0 && isMouseOnComponent(mouseX - x, mouseY - y)) {
            onClick.accept(Minecraft.getMinecraft().player)

            /*component.redstoneMode = RedstoneModeTileEntityComponent.RedstoneMode.values()[(component.redstoneMode.ordinal + 1) % RedstoneModeTileEntityComponent.RedstoneMode.values().size]
            val tag = NBTTagCompound()
            tag.setTag("component", component.serializeNBT())
            if (component.tile is MultiblockTileEntityBase) { // not tested yet for multiblocks (because multiblocks currently haven't redstonemode stuff)
                tag.setString("name", ((component.tile as MultiblockTileEntityBase).multiblockController as BaseMultiBlock).getComponents().filter { it.second == component }[0].first)
            } else if (component.tile is TCAggregatable) {
                tag.setString("name", (component.tile as TCAggregatable).getComponents().filter { it.second == component }[0].first)
            }
            tag.setLong("pos", component.tile.pos.toLong())
            PacketHandler.sendToServer(ClientComponentUpdatePacket(tag))*/

        }
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height
    }
}