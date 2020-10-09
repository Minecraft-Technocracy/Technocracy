package net.cydhra.technocracy.foundation.client.gui.components.upgradelist

import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicItemHolderCapability


abstract class UpgradeList(override var posX: Int, override var posY: Int, val capability: DynamicItemHolderCapability) : TCComponent() {
    override var width = 130
    override var height = 85

    override fun update() {
    }

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height
    }
}