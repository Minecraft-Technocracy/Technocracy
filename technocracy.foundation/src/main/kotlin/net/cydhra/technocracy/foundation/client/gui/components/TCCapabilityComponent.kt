package net.cydhra.technocracy.foundation.client.gui.components

import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractTileEntityCapabilityComponent


abstract class TCCapabilityComponent<T : AbstractTileEntityCapabilityComponent>(val component: T) : TCComponent() {
}