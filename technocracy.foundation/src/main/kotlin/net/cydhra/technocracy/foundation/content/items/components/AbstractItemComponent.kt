package net.cydhra.technocracy.foundation.content.items.components

import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.items.capability.ItemCapabilityWrapper


abstract class AbstractItemComponent : IComponent {

    lateinit var wrapper: ItemCapabilityWrapper

    var needsClientSyncing = false

    override fun markDirty(needsClientRerender: Boolean) {
        wrapper.updateItemStack()
    }

    override fun onRegister() {
        // do nothing by default
    }

    override fun onLoadAggregate() {
        // do nothing by default
    }
}