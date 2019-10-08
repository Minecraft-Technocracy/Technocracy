package net.cydhra.technocracy.foundation.items.capability

import net.cydhra.technocracy.foundation.tileentity.components.AbstractCapabilityComponent


abstract class AbstractItemCapabilityComponent : AbstractCapabilityComponent() {

    lateinit var wrapper: ItemCapabilityWrapper

    override fun markDirty(needsClientRerender: Boolean) {
        wrapper.updateItemStack()
    }
}