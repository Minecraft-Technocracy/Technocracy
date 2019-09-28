package net.cydhra.technocracy.astronautics.tileentity

import net.cydhra.technocracy.foundation.tileentity.AggregatableTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.OwnerShipComponent

class TileEntityRocketController : AggregatableTileEntity() {
    val ownerShip = OwnerShipComponent()

    init {
        registerComponent(ownerShip, "ownership")
    }
}