package net.cydhra.technocracy.foundation.content.tileentities.multiblock.saline

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.multiblock.SalineMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.HeatStorageTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart

class TileEntitySalineController :
        TileEntityMultiBlockPart<SalineMultiBlock>(SalineMultiBlock::class, ::SalineMultiBlock),
        ITileEntityMultiblockController {

    //Fluids
    val fluidInputComponent = FluidTileEntityComponent(capacity = 8000, facing = mutableSetOf(),
            tanktype = DynamicFluidCapability.TankType.INPUT)
    val fluidOutputComponent = FluidTileEntityComponent(capacity = 8000, facing = mutableSetOf(),
            tanktype = DynamicFluidCapability.TankType.OUTPUT)

    //Heat
    val heatComponent = HeatStorageTileEntityComponent(0, 1000)
    val heatingFluidInputComponent = FluidTileEntityComponent(capacity = 8000, facing = mutableSetOf(),
            tanktype = DynamicFluidCapability.TankType.INPUT)
    val heatingFluidOutputComponent = FluidTileEntityComponent(capacity = 8000, facing = mutableSetOf(),
            tanktype = DynamicFluidCapability.TankType.OUTPUT)

    init {
        registerComponent(fluidInputComponent, "input")
        registerComponent(heatingFluidInputComponent, "heat_input")
        registerComponent(heatComponent, "heat")
        registerComponent(fluidOutputComponent, "output")
        registerComponent(heatingFluidOutputComponent, "heat_output")
    }
}