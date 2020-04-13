package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.api.ecs.tileentities.AbstractTileEntityComponent
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Defines structure for capabilities that also implement a capability.
 */
abstract class AbstractTileEntityCapabilityComponent : AbstractTileEntityComponent(), ICapabilityProvider {

}