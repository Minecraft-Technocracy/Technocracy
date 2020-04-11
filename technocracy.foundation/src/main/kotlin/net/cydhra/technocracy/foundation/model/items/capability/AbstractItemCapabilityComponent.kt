package net.cydhra.technocracy.foundation.model.items.capability

import net.cydhra.technocracy.foundation.content.items.components.AbstractItemComponent
import net.minecraftforge.common.capabilities.ICapabilityProvider


abstract class AbstractItemCapabilityComponent : AbstractItemComponent(), ICapabilityProvider {
    override fun markDirty(needsClientRerender: Boolean) {
        wrapper.updateItemStack()
    }
}