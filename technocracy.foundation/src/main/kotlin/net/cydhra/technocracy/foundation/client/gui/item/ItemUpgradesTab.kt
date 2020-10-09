package net.cydhra.technocracy.foundation.client.gui.item

import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.label.WrappingLabel
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.components.upgradelist.DefaultUpgradeList
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.content.items.upgradeFrameItem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.entity.player.EntityPlayer
import java.awt.Color

//todo remove and make one class with MachineUpgradesTab
class ItemUpgradesTab(parent: TCGui,
                      private val upgrades: ItemUpgradesComponent,
                      private val player: EntityPlayer?) :
        TCTab("Upgrades", parent, icon = TCIcon(upgradeFrameItem)) {

    companion object {
        const val PADDING_LEFT = 8
        const val PADDING_TOP = 20
        const val PADDING_RIGHT = PADDING_LEFT
        const val SLOT_WIDTH_PLUS_PADDING = 18
        const val UPGRADE_SLOTS_PER_ROW = 3
        const val INFO_LABEL_OFFSET = PADDING_LEFT + UPGRADE_SLOTS_PER_ROW * SLOT_WIDTH_PLUS_PADDING + PADDING_LEFT
    }

    private val lableWidth = (parent.guiWidth - PADDING_RIGHT - INFO_LABEL_OFFSET) / 2
    private val infoTitleLabel = WrappingLabel(
            posX = INFO_LABEL_OFFSET,
            posY = PADDING_TOP,
            maxWidth = lableWidth,
            scaling = 0.7,
            text = "",
            gui = parent
    )

    private val infoContentLabel = WrappingLabel(
            posX = INFO_LABEL_OFFSET + lableWidth,
            posY = PADDING_TOP,
            maxWidth = lableWidth,
            scaling = 0.7,
            text = "",
            gui = parent
    )

    override fun init() {
        if (player != null) {
            addPlayerInventorySlots(player, PADDING_LEFT, parent.guiHeight - 58 - 16 - 5 - 12)
        }

        //for (i in 0 until upgrades.numberOfUpgradeSlots) {
        components.add(TCSlotIO(upgrades.dummySlot, 0,
                PADDING_LEFT + (0 % UPGRADE_SLOTS_PER_ROW) * SLOT_WIDTH_PLUS_PADDING,
                PADDING_TOP + (0 / UPGRADE_SLOTS_PER_ROW) * SLOT_WIDTH_PLUS_PADDING, parent))
        //}

        components.add(infoTitleLabel)
        components.add(infoContentLabel)

        components.add(DefaultUpgradeList(PADDING_LEFT * 2 + SLOT_WIDTH_PLUS_PADDING, PADDING_TOP -1, upgrades.itemHolder, parent))

    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val infoText = StringBuffer()
        upgrades.description.forEach { (title, _) ->
            infoText.append(title.formattedText + "\n")
        }
        this.infoTitleLabel.text = infoText.toString()

        infoText.setLength(0)
        upgrades.description.forEach { (_, data) ->
            infoText.append(data.formattedText + "\n")
        }
        this.infoContentLabel.text = infoText.toString()

        /*Gui.drawRect(x + PADDING_LEFT + UPGRADE_SLOTS_PER_ROW + SLOT_WIDTH_PLUS_PADDING, y + PADDING_TOP - 1, x + parent.guiWidth - PADDING_LEFT, y + parent.guiHeight - 58 - 16 - 5 - 12 - 4, Color.BLACK.rgb)

        var cardOffset = 2
        val cardheight = 18

        val offX = x + PADDING_LEFT + UPGRADE_SLOTS_PER_ROW + SLOT_WIDTH_PLUS_PADDING + 3
        val offY = y + PADDING_TOP - 1 + cardOffset

        for (stack in upgrades.itemHolder.getStacks()) {
            Gui.drawRect(offX, offY, x + parent.guiWidth - PADDING_LEFT - 3, offY + cardheight, Color.GRAY.rgb)
            Minecraft.getMinecraft().renderItem.renderItemIntoGUI(stack, x + PADDING_LEFT + UPGRADE_SLOTS_PER_ROW + SLOT_WIDTH_PLUS_PADDING + 3 + 1, y + PADDING_TOP - 1 + cardOffset + 1)

            if (isMouseOnComponent(mouseX, mouseY, offX + 1, offY + 1, 16, 16)) {
                (parent as TCClientGuiImpl).renderToolTip(stack, mouseX, mouseY)
            }

            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(stack.displayName, offX + 16 + 2f,offY + 9 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f, -1)

            cardOffset += cardheight + 2
        }*/

        super.draw(x, y, mouseX, mouseY, partialTicks)
    }

    fun isMouseOnComponent(mouseX: Int, mouseY: Int, posX: Int, posY: Int, width: Int, height: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }
}