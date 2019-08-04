package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.blocks.DrumBlock
import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing


class TileEntityDrum() : AggregatableTileEntity() {
    val fluidCapability = DynamicFluidHandler(0, mutableListOf())

    private val fluidComponent = FluidComponent(fluidCapability, EnumFacing.values().toMutableSet())

    override fun onLoad() {
        val state = world.getBlockState(getPos())
        fluidCapability.capacity = DrumBlock.DrumType.values()[state.block.getMetaFromState(state)].amount
    }

    init {
        this.registerComponent(fluidComponent, "fluid")
    }
}