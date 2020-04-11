package net.cydhra.technocracy.foundation.content.capabilities

import net.cydhra.technocracy.foundation.api.ecs.IComponent

/**
 * Links a capability with its component. Used to inform the component that the capability got dirty
 */
abstract class AbstractComponentCapabilityBridge {
    lateinit var componentParent: IComponent

    open fun markDirty(needsClientRerender: Boolean = false) {
        componentParent.markDirty(needsClientRerender)
    }
}