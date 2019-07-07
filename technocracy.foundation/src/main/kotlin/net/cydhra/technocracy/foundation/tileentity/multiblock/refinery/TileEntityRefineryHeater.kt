package net.cydhra.technocracy.foundation.tileentity.multiblock.refinery

import net.cydhra.technocracy.foundation.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.tileentity.AggregatableDelegate
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatable
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class TileEntityRefineryHeater : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class,
        ::RefineryMultiBlock), TCAggregatable by AggregatableDelegate() {

}