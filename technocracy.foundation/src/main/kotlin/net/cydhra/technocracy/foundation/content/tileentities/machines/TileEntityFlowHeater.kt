package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.util.EnumFacing

/**
 * A heater tile entity that heats incoming liquids using energy.
 */
class TileEntityFlowHeater : MachineTileEntity() {
    private val inputFluidComponent = TileEntityFluidComponent(
        4000,
        tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.WEST)
    )

    private val outputFluidComponent = TileEntityFluidComponent(
        4000,
        tanktype = DynamicFluidCapability.TankType.OUTPUT, facing = mutableSetOf(EnumFacing.EAST)
    )

    init {
        this.registerComponent(inputFluidComponent, "input_fluid")
        this.registerComponent(outputFluidComponent, "output_fluid")

        this.addLogicStrategy(
            ItemProcessingLogic(
                recipeType = RecipeManager.RecipeType.FLOW_HEATER,
                inputFluidSlots = arrayOf(this.inputFluidComponent.fluid),
                outputFluidSlots = arrayOf(this.outputFluidComponent.fluid),
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                baseTickEnergyCost = 60,
                progress = this.progressComponent
            ), MACHINE_PROCESSING_LOGIC_NAME
        )
    }
}