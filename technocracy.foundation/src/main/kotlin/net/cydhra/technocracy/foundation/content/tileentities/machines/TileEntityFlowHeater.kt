package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_CONVERSION
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_ENERGY
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_SPEED
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.tileentities.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityHeatStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.CoolingLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.ProduceHeatLogic
import net.minecraft.util.EnumFacing

/**
 * A heater tile entity that heats incoming liquids using heat generated from electricity.
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

    private val heatComponent = TileEntityHeatStorageComponent(0, 100_000)

    init {
        this.removeComponent("progress")
        this.removeComponent("processing_cost")

        this.upgradeParameters.remove(UPGRADE_ENERGY)
        this.upgradeParameters.remove(UPGRADE_SPEED)
        this.upgradeParameters[UPGRADE_CONVERSION] = processingSpeedComponent

        this.registerComponent(heatComponent, "heat_buffer")
        this.registerComponent(inputFluidComponent, "input_fluid")
        this.registerComponent(outputFluidComponent, "output_fluid")

        this.addLogicStrategy(
            CoolingLogic(
                10,
                hotFluidComponent = this.outputFluidComponent,
                coldFluidComponent = this.inputFluidComponent,
                heatBuffer = this.heatComponent
            ),
            "heat_processing"
        )

        this.addLogicStrategy(
            ProduceHeatLogic(
                heatBuffer = this.heatComponent,
                baseEnergyConsumption = 5,
                speedMultiplierComponent = processingSpeedComponent,
                energyComponent = energyStorageComponent
            ),
            "heat_generating"
        )
    }
}