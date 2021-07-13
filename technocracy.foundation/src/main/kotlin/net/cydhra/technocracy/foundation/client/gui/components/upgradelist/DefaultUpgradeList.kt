package net.cydhra.technocracy.foundation.client.gui.components.upgradelist

import net.cydhra.technocracy.foundation.api.ecs.IAggregatable
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicItemHolderCapability
import net.cydhra.technocracy.foundation.util.opengl.Stencil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.Integer.max
import java.lang.Integer.min


class DefaultUpgradeList(posX: Int, posY: Int, component: DynamicItemHolderCapability, override var gui: TCGui) : UpgradeList(posX, posY, component) {

    var scrollIndex = 0

    override fun onMouseScroll(dir: Int) {
        if (dir > 0) {
            scrollIndex -= 10
        } else if (dir < 0) {
            scrollIndex += 10
        }

        scrollIndex = min(scrollIndex, capability.getStacks().size * 20 - (height - 4))
        scrollIndex = max(scrollIndex, 0)
    }

    override fun update() {
        scrollIndex = min(scrollIndex, capability.getStacks().size * 20 - (height - 4))
        scrollIndex = max(scrollIndex, 0)
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val x = x + posX
        val y = y + posY

        Gui.drawRect(x, y, x + width, y + height, Color.BLACK.rgb)

        Stencil(Minecraft.getMinecraft().framebuffer) {

            clear(false)
            GL11.glEnable(GL11.GL_STENCIL_TEST)
            func(GL11.GL_ALWAYS, true)
            op(GL11.GL_KEEP, GL11.GL_REPLACE, GL11.GL_REPLACE)

            Gui.drawRect(x, y, x + width, y + height, Color.BLACK.rgb)

            op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP)
            func(GL11.GL_EQUAL, true)

            val cardOffset = 2
            val cardheight = 18

            val offX = x + 3
            var offY = y + cardOffset - scrollIndex

            for (pair in capability.getStacks().groupBy { it.item }) {
                if (isMouseOnComponent(mouseX, mouseY, offX, offY, width - 3, cardheight)) {
                    Gui.drawRect(offX, offY, x + width - 3, offY + cardheight, Color.RED.rgb)
                } else {
                    Gui.drawRect(offX, offY, x + width - 3, offY + cardheight, Color.GRAY.rgb)
                }

                Minecraft.getMinecraft().renderItem.renderItemIntoGUI(pair.value.first(), offX + 1, offY + 1)
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("${pair.value.size}x ${pair.key.defaultInstance.displayName}", offX + 16 + 3f, offY + 9 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f, -1)

                offY += cardheight + cardOffset
            }

            func(GL11.GL_ALWAYS, true)
            op(GL11.GL_KEEP, GL11.GL_REPLACE, GL11.GL_REPLACE)
            GL11.glDisable(GL11.GL_STENCIL_TEST)
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

        for (pair in capability.getStacks().groupBy { it.item }) {
            if (isMouseOnComponent(mouseX - posX, mouseY - posY, offX + 1, offY + 1 - scrollIndex, 16, 16)) {
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

        for (pair in capability.getStacks().withIndex().groupBy { it.value.item }) {
            if (isMouseOnComponent(mouseX, mouseY, offX, offY - scrollIndex, width - 3, cardheight)) {
                super.mouseClicked(x, y, mouseX, mouseY, pair.value.first().index)
                return
            }
            offY += cardheight + cardOffset
        }

        //super.mouseClicked(x, y, mouseX, mouseY, mouseButton)
    }
}