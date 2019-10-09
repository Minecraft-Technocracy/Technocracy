package net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.label.DefaultLabel
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.content.fluids.heavyOilFluid
import net.cydhra.technocracy.foundation.content.fluids.lightOilFluid
import net.cydhra.technocracy.foundation.content.fluids.mineralOilFluid
import net.cydhra.technocracy.foundation.content.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.ProgressComponent
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

    val inputComponent = FluidComponent(internalFluidHandler, mutableSetOf(EnumFacing.NORTH, EnumFacing.EAST,
            EnumFacing.SOUTH, EnumFacing.WEST))
    private val topOutput = FluidComponent(topTank, facing = mutableSetOf(*EnumFacing.values()))
    private val bottomOutput = FluidComponent(bottomTank, facing = mutableSetOf(*EnumFacing.values()))
    private val progressComponent = ProgressComponent()

    init {
        this.registerComponent(inputComponent, "input")
        this.registerComponent(topOutput, "top")
        this.registerComponent(bottomOutput, "bottom")
        this.registerComponent(progressComponent, "progress")
    }

    override fun initGui(gui: TCGui) {
        gui.registerTab(object: TCTab("Example", gui) {
            override fun init() {
                addComponent(DefaultLabel(10, 20, "Hello World"))
            }
        })
    }
}