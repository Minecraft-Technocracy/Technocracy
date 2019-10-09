package net.cydhra.technocracy.foundation.model.items.capability

import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractCapabilityComponent


abstract class AbstractItemCapabilityComponent : AbstractCapabilityComponent() {

    lateinit var wrapper: ItemCapabilityWrapper

    override fun markDirty(needsClientRerender: Boolean) {
        wrapper.updateItemStack()
    }
}