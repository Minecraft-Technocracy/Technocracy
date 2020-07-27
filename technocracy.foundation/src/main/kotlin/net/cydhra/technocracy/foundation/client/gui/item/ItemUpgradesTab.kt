package net.cydhra.technocracy.foundation.client.gui.item

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.label.WrappingLabel
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.content.items.upgradeFrameItem
import net.minecraft.entity.player.EntityPlayer

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

        for (i in 0 until upgrades.numberOfUpgradeSlots) {
            components.add(TCSlotIO(upgrades.inventory, i,
                    PADDING_LEFT + (i % UPGRADE_SLOTS_PER_ROW) * SLOT_WIDTH_PLUS_PADDING,
                    PADDING_TOP + (i / UPGRADE_SLOTS_PER_ROW) * SLOT_WIDTH_PLUS_PADDING, parent))
        }

        components.add(infoTitleLabel)
        components.add(infoContentLabel)
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


        super.draw(x, y, mouseX, mouseY, partialTicks)
    }
}