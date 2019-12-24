package net.cydhra.technocracy.foundation.content.tileentities.storage

import net.cydhra.technocracy.foundation.content.blocks.DrumBlock
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.minecraft.util.EnumFacing


class TileEntityDrum() : AggregatableTileEntity() {
    val fluidCapability = DynamicFluidCapability(0, mutableListOf())

    private val fluidComponent = FluidTileEntityComponent(fluidCapability, EnumFacing.values().toMutableSet())

    override fun onLoad() {
        val state = world.getBlockState(getPos())
        fluidCapability.capacity = DrumBlock.DrumType.values()[state.block.getMetaFromState(state)].amount
    }

    init {
        fluidComponent.syncToClient = true
        this.registerComponent(fluidComponent, "fluid")
    }
}