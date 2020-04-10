package net.cydhra.technocracy.foundation.content.capabilities

import net.cydhra.technocracy.foundation.api.ecs.tileentities.AbstractTileEntityComponent

/**
 * Superclass to capability handlers that can mark a [parent][AbstractTileEntityComponent] dirty upon mutation. To
 * mark a parent dirty, call [markDirty] with a flag whether the client needs to update rendering.
 */
abstract class AbstractMutableCapabilityHandler {
    lateinit var componentParent: AbstractTileEntityComponent

    open fun markDirty(needsClientRerender: Boolean = false) {
        componentParent.markDirty(needsClientRerender)
    }
}