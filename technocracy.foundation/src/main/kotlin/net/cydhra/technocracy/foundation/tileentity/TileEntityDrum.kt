package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.blocks.DrumBlock
import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.minecraft.util.EnumFacing


class TileEntityDrum(val type: Int = -1) : AggregatableTileEntity() {

    val fluidCapability = DynamicFluidHandler(DrumBlock.DrumType.values()[type].amount, mutableListOf())

    private val fluidComponent = FluidComponent(fluidCapability, EnumFacing.values().toMutableSet())

    init {
        this.registerComponent(fluidComponent, "fluid")
    }
}