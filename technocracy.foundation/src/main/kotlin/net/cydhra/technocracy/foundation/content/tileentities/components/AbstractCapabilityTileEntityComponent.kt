package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Defines structure for capabilities that also implement a capability.
 */
abstract class AbstractCapabilityTileEntityComponent : AbstractTileEntityComponent(), ICapabilityProvider {

}