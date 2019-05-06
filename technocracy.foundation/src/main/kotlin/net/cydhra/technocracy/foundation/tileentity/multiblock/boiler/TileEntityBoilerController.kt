package net.cydhra.technocracy.foundation.tileentity.multiblock.boiler

import net.cydhra.technocracy.foundation.multiblock.BoilerMultiBlock
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart

/**
 * The tile entity for the controller block of a boiler multi-block structure
 */
class TileEntityBoilerController : TileEntityMultiBlockPart<BoilerMultiBlock>(BoilerMultiBlock::class, ::BoilerMultiBlock) {
    override fun onMachineActivated() {

    }

    override fun onMachineDeactivated() {

    }
}