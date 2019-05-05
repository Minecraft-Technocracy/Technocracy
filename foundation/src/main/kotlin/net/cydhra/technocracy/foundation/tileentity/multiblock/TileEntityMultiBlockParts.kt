package net.cydhra.technocracy.foundation.tileentity.multiblock

import net.cydhra.technocracy.foundation.multiblock.BoilerMultiBlock

/*
 * This file contains all subclasses of TileEntityMultiBlockPart, as they do not define behavior anyway and the only
 * difference is the multiblock controller they return
 */

class TileEntityMultiBlockPartBoiler
    : TileEntityMultiBlockPart<BoilerMultiBlock>(BoilerMultiBlock::class, ::BoilerMultiBlock)