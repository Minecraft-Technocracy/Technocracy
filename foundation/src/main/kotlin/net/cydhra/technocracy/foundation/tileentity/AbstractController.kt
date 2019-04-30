package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.components.RedstoneModeComponent
import net.minecraft.util.EnumFacing

/**
 * Abstract base class for tile entity that are multi-block structure controllers
 */
abstract class AbstractController : AbstractComponentTileEntity() {

    /**
     * The machine's redstone mode
     */
    protected val redstoneModeComponent = RedstoneModeComponent()

    /**
     * The machines internal energy storage and transfer limit state
     */
    protected val energyStorageComponent = EnergyStorageComponent(mutableSetOf(EnumFacing.UP))
}