package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * Defines structure for capabilities that also implement a capability.
 */
abstract class AbstractCapabilityTileEntityComponent : AbstractTileEntityComponent() {

    /**
     * Returns true, if this component implements the capability that is given through [capability].
     *
     * @param capability the queried capability
     * @param facing for side-depending capabilities the queried side is given
     *
     * @return true, if this component offers the queried capability on given block face
     */
    abstract fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean

    /**
     * Get the capability instance of the queried capability casted to [T]
     *
     * @param capability queried capability
     * @param facing queried block face
     * @param T runtime type of requested capability that is expected by caller
     */
    abstract fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T?
}