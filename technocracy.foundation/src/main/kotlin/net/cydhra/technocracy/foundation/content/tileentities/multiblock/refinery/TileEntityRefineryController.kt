package net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.fluids.heavyOilFluid
import net.cydhra.technocracy.foundation.content.fluids.lightOilFluid
import net.cydhra.technocracy.foundation.content.fluids.mineralOilFluid
import net.cydhra.technocracy.foundation.content.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityProgressComponent
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing

class TileEntityRefineryController : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class,
        ::RefineryMultiBlock), ITileEntityMultiblockController {

    val topTank = DynamicFluidCapability(4000, allowedFluid = mutableListOf(),
            tanktype = DynamicFluidCapability.TankType.OUTPUT)
    val bottomTank = DynamicFluidCapability(4000, allowedFluid = mutableListOf(),
            tanktype = DynamicFluidCapability.TankType.OUTPUT)

    private val internalFluidHandler = DynamicFluidCapability(4000,
            allowedFluid = mutableListOf(mineralOilFluid.hotFluid.name, heavyOilFluid.hotFluid.name,
                    lightOilFluid.hotFluid.name),
            tanktype = DynamicFluidCapability.TankType.INPUT)

    val inputComponent = TileEntityFluidComponent(internalFluidHandler, mutableSetOf(EnumFacing.NORTH, EnumFacing.EAST,
            EnumFacing.SOUTH, EnumFacing.WEST))
    private val topOutput = TileEntityFluidComponent(topTank, facing = mutableSetOf(*EnumFacing.values()))
    private val bottomOutput = TileEntityFluidComponent(bottomTank, facing = mutableSetOf(*EnumFacing.values()))
    private val progressComponent = TileEntityProgressComponent()

    init {
        this.registerComponent(inputComponent, "input")
        this.registerComponent(topOutput, "top")
        this.registerComponent(bottomOutput, "bottom")
        this.registerComponent(progressComponent, "progress")
    }

}