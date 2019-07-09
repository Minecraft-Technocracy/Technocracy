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

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class TileEntityRefineryController : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class,
        ::RefineryMultiBlock), TCAggregatable by AggregatableDelegate() {

    val topTank = DynamicFluidHandler(4000, allowedFluid = mutableListOf(),
            tanktype = DynamicFluidHandler.TankType.OUTPUT)
    val bottomTank = DynamicFluidHandler(4000, allowedFluid = mutableListOf(),
            tanktype = DynamicFluidHandler.TankType.OUTPUT)

    private val internalFluidHandler = DynamicFluidHandler(4000,
            allowedFluid = mutableListOf(mineralOilFluid.hotFluid.name, heavyOilFluid.hotFluid.name,
                    lightOilFluid.hotFluid.name),
            tanktype = DynamicFluidHandler.TankType.INPUT)

    val inputComponent = FluidComponent(internalFluidHandler, mutableSetOf(EnumFacing.NORTH, EnumFacing.EAST,
            EnumFacing.SOUTH, EnumFacing.WEST))
    private val topOutput = FluidComponent(topTank, facing = mutableSetOf(*EnumFacing.values()))
    private val bottomOutput = FluidComponent(bottomTank, facing = mutableSetOf(*EnumFacing.values()))

    init {
        this.registerComponent(inputComponent, "input")
        this.registerComponent(topOutput, "top")
        this.registerComponent(bottomOutput, "bottom")
    }

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        return this.serializeNBT(super.writeToNBT(data))
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        this.deserializeNBT(data)
    }
}