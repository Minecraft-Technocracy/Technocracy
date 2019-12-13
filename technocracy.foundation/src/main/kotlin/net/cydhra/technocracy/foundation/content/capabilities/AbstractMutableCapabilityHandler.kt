package net.cydhra.technocracy.foundation.content.capabilities

import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent

/**
 * Superclass to capability handlers that can mark a [parent][AbstractComponent] dirty upon mutation. To
 * mark a parent dirty, call [markDirty] with a flag whether the client needs to update rendering.
 */
abstract class AbstractMutableCapabilityHandler {
    lateinit var componentParent: AbstractComponent

    open fun markDirty(needsClientRerender: Boolean = false) {
        componentParent.markDirty(needsClientRerender)
    }
}