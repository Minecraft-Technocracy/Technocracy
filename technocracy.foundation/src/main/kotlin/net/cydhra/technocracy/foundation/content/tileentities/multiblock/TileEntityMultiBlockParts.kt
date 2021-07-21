package net.cydhra.technocracy.foundation.content.tileentities.multiblock

import net.cydhra.technocracy.foundation.content.multiblock.CapacitorMultiBlock
import net.cydhra.technocracy.foundation.content.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.content.multiblock.SalineMultiBlock

/*
 * This file contains all subclasses of TileEntityMultiBlockPart, as they do not define behavior anyway and the only
 * difference is the multiblock controller they return
 */
class TileEntityMultiBlockPartRefinery
    : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class, ::RefineryMultiBlock)

class TileEntityMultiBlockPartCapacitor
    : TileEntityMultiBlockPart<CapacitorMultiBlock>(CapacitorMultiBlock::class, ::CapacitorMultiBlock)

class TileEntityMultiBlockPartSaline
    : TileEntityMultiBlockPart<SalineMultiBlock>(SalineMultiBlock::class, ::SalineMultiBlock)