package net.cydhra.technocracy.foundation.client.gui.components

import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractCapabilityTileEntityComponent


abstract class TCCapabilityComponent<T : AbstractCapabilityTileEntityComponent>(val component: T) : TCComponent() {
}