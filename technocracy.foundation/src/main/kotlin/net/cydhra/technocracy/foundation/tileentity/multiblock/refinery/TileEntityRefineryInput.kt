package net.cydhra.technocracy.foundation.tileentity.multiblock.refinery

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.liquids.general.heavyOilFluid
import net.cydhra.technocracy.foundation.liquids.general.lightOilFluid
import net.cydhra.technocracy.foundation.liquids.general.mineralOilFluid
import net.cydhra.technocracy.foundation.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.tileentity.AggregatableDelegate
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatable
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class TileEntityRefineryInput : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class,
        ::RefineryMultiBlock), TCAggregatable by AggregatableDelegate() {

    private val internalFluidHandler = DynamicFluidHandler(4000,
            allowedFluid = mutableListOf(mineralOilFluid.hotFluid.name, heavyOilFluid.hotFluid.name,
                    lightOilFluid.hotFluid.name),
            tanktype = DynamicFluidHandler.TankType.INPUT)

    val fluidComponent = FluidComponent(internalFluidHandler, mutableSetOf(EnumFacing.NORTH, EnumFacing.EAST,
            EnumFacing.SOUTH, EnumFacing.WEST))

    init {
        registerComponent(fluidComponent, "input")
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return this.supportsCapability(capability, facing) || super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return this.castCapability(capability, facing) ?: super.getCapability(capability, facing)
    }

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        return this.serializeNBT(super.writeToNBT(data))
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        this.deserializeNBT(data)
    }
}