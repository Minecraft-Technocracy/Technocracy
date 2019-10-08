package net.cydhra.technocracy.foundation.tileentity.multiblock.refinery

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.label.DefaultLabel
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.liquids.general.heavyOilFluid
import net.cydhra.technocracy.foundation.liquids.general.lightOilFluid
import net.cydhra.technocracy.foundation.liquids.general.mineralOilFluid
import net.cydhra.technocracy.foundation.multiblock.RefineryMultiBlock
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.cydhra.technocracy.foundation.tileentity.components.ProgressComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

class TileEntityRefineryController : TileEntityMultiBlockPart<RefineryMultiBlock>(RefineryMultiBlock::class,
        ::RefineryMultiBlock), ITileEntityMultiblockController {

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