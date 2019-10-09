package net.cydhra.technocracy.foundation.capabilities

import net.cydhra.technocracy.foundation.tileentity.components.AbstractCapabilityComponent

/**
 * Superclass to capability handlers that can mark a [parent][AbstractCapabilityComponent] dirty upon mutation. To
 * mark a parent dirty, call [markDirty] with a flag whether the client needs to update rendering.
 */
abstract class AbstractMutableCapabilityHandler {
    lateinit var componentParent: AbstractCapabilityComponent

    open fun markDirty(needsClientRerender: Boolean = false) {
        componentParent.markDirty(needsClientRerender)
    }
}