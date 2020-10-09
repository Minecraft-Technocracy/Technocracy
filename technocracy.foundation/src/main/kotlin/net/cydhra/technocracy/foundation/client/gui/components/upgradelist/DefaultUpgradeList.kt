package net.cydhra.technocracy.foundation.client.gui.components.upgradelist

import net.cydhra.technocracy.foundation.api.ecs.IAggregatable
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.item.ItemUpgradesTab
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicItemHolderCapability
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import java.awt.Color


class DefaultUpgradeList(posX: Int, posY: Int, component: DynamicItemHolderCapability, override var gui: TCGui) : UpgradeList(posX, posY, component) {
    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {

        val x = x + posX
        val y = y + posY

        Gui.drawRect(x, y, x + width, y + height, Color.BLACK.rgb)

        val cardOffset = 2
        val cardheight = 18

        val offX = x + 3
        var offY = y + cardOffset

        for(pair in capability.getStacks().groupBy { it.item }) {
            if (isMouseOnComponent(mouseX, mouseY, offX, offY , width - 3, cardheight)) {
                Gui.drawRect(offX, offY, x + width - 3, offY + cardheight, Color.RED.rgb)
            } else {
                Gui.drawRect(offX, offY, x + width - 3, offY + cardheight, Color.GRAY.rgb)
            }

            Minecraft.getMinecraft().renderItem.renderItemIntoGUI(pair.value.first(), offX +1, offY +1)
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(pair.key.defaultInstance.displayName + " x${pair.value.size}", offX + 16 + 3f, offY + 9 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f, -1)

            offY += cardheight + cardOffset
        }
    }

    override var onClick: ((Side, EntityPlayer, IAggregatable?, Int) -> Unit)? = { side: Side, player: EntityPlayer, tileEntity: IAggregatable?, button: Int ->
        if (side == Side.SERVER) {
            val stack = component.getStacks()[button]
            component.removeStack(stack)
            player.inventory.addItemStackToInventory(stack)
        }
    }

    fun isMouseOnComponent(mouseX: Int, mouseY: Int, posX: Int, posY: Int, width: Int, height: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val x = 0
        val y = 0

        val cardOffset = 2
        val cardheight = 18

        val offX = x + 3
        var offY = y + cardOffset

        for(pair in capability.getStacks().groupBy { it.item }) {
            if (isMouseOnComponent(mouseX - posX, mouseY - posY, offX + 1, offY + 1, 16, 16)) {
                (gui as TCClientGuiImpl).renderToolTip(pair.value.first(), mouseX, mouseY)
            }
            offY += cardheight + cardOffset
        }
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {
        val x = x + posX
        val y = y + posY

        val cardOffset = 2
        val cardheight = 18

        val offX = x + 3
        var offY = y + cardOffset

        for(pair in capability.getStacks().withIndex().groupBy { it.value.item }) {
            if (isMouseOnComponent(mouseX, mouseY, offX, offY, width - 3, cardheight)) {
                super.mouseClicked(x, y, mouseX, mouseY, pair.value.first().index)
                return
            }
            offY += cardheight + cardOffset
        }

        //super.mouseClicked(x, y, mouseX, mouseY, mouseButton)
    }
}