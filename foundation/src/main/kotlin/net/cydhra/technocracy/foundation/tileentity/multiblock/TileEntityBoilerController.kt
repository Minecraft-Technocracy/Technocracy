package net.cydhra.technocracy.foundation.tileentity.multiblock

import net.cydhra.technocracy.foundation.multiblock.BoilerMultiBlock

/**
 * The tile entity for the controller block of a boiler multi-block structure
 */
class TileEntityBoilerController : TileEntityMultiBlockPart<BoilerMultiBlock>(BoilerMultiBlock::class, ::BoilerMultiBlock) {
    override fun onMachineActivated() {

    }

    override fun onMachineDeactivated() {

    }
}