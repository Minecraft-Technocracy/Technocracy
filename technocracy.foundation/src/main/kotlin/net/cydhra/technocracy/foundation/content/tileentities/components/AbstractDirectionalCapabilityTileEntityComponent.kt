package net.cydhra.technocracy.foundation.content.tileentities.components

import net.minecraft.util.EnumFacing

/**
 * Defines structure for capabilities with a facing that also implement a capability.
 */
abstract class AbstractDirectionalCapabilityTileEntityComponent: AbstractCapabilityTileEntityComponent() {
    abstract val facing: MutableSet<EnumFacing>

    abstract fun getDirection() : Direction

    enum class Direction {
        INPUT, OUTPUT, BOTH, NONE
    }
}