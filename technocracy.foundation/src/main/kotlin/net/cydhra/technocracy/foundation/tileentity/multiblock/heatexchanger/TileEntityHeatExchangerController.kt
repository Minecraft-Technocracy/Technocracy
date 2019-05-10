package net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger

import net.cydhra.technocracy.foundation.multiblock.HeatExchangerMultiBlock
import net.cydhra.technocracy.foundation.tileentity.AggregatableDelegate
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatable
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class TileEntityHeatExchangerController : TileEntityMultiBlockPart<HeatExchangerMultiBlock>(HeatExchangerMultiBlock::class,
        ::HeatExchangerMultiBlock), TCAggregatableTileEntity, TCAggregatable by AggregatableDelegate() {

}