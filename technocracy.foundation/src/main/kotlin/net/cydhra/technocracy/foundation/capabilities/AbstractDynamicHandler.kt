package net.cydhra.technocracy.foundation.capabilities

import net.cydhra.technocracy.foundation.tileentity.components.AbstractCapabilityComponent


abstract class AbstractDynamicHandler {
    lateinit var componentParent: AbstractCapabilityComponent

    open fun markDirty(needsClientRerender: Boolean = false) {
        componentParent.markDirty(needsClientRerender)
    }
}