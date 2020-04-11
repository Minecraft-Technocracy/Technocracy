package net.cydhra.technocracy.foundation.model.items.capability

import net.cydhra.technocracy.foundation.api.ecs.IComponent


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