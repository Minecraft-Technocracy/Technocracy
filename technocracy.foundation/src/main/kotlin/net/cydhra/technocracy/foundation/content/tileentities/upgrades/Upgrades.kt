package net.cydhra.technocracy.foundation.content.tileentities.upgrades

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.fluids.heatTransferOilFluid
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.HeatStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MultiplierTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.AdditiveConsumptionLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.CoolingLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.GenerateHeatLogic
import net.cydhra.technocracy.foundation.model.tileentities.api.TCMachineTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgrade
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeParameter
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MultiplierUpgrade
import net.minecraft.util.EnumFacing

const val MACHINE_UPGRADE_ENERGY: MachineUpgradeParameter = "energy"
const val MACHINE_UPGRADE_SPEED: MachineUpgradeParameter = "speed"
const val MACHINE_UPGRADE_ADDITIVE_CONSUMPTION: MachineUpgradeParameter = "additive"

/**
 * This parameter type is used for all upgrades that do something to the machine but do not rely on a specific
 * machine parameter to be present. For example, [LubricantUpgrade] is a generic-parameter upgrade, as it can be
 * used on all machines per se.
 */
const val MACHINE_UPGRADE_GENERIC: MachineUpgradeParameter = "generic"

/**
 * A multiplier on machine energy consumption
 */
class EnergyMultiplier(multiplier: Double) : MultiplierUpgrade(multiplier, MACHINE_UPGRADE_ENERGY)

/**
 * A multiplier on machine processing speed
 */
class SpeedMultiplier(multiplier: Double) : MultiplierUpgrade(multiplier, MACHINE_UPGRADE_SPEED)

/**
 * A multiplier on how much of a machine's additive is consumed per tick of work.
 */
class AdditiveConsumptionMultiplier(multiplier: Double) : MultiplierUpgrade(multiplier, MACHINE_UPGRADE_ADDITIVE_CONSUMPTION)

/**
 * An upgrade that enables the machine to use lubricant additives for speed increase.
 */
class LubricantUpgrade : MachineUpgrade(MACHINE_UPGRADE_GENERIC) {

    companion object {
        const val LUBRICANT_FLUID_COMPONENT_NAME = "special_lubricant"
        const val LUBRICANT_MULTIPLIER_COMPONENT_NAME = "special_lubricant_multiplier"
        const val LUBRICANT_CONSUMPTION_LOGIC_NAME = "special_lubricant_consumption"
        const val LUBRICANT_BASE_USAGE = 4
    }

    override fun canInstallUpgrade(tile: TCMachineTileEntity,
                                   upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return upgrades.getInstalledUpgrades()
                .filterIsInstance<LubricantUpgrade>()
                .isEmpty()
    }

    override fun onInstallUpgrade(tile: TCMachineTileEntity,
                                  upgrades: MachineUpgradesTileEntityComponent) {
        this.onUpgradeLoad(tile, upgrades)
    }

    override fun onUninstallUpgrade(tile: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        tile.removeComponent(LUBRICANT_FLUID_COMPONENT_NAME)
        tile.removeComponent(LUBRICANT_MULTIPLIER_COMPONENT_NAME)
        tile.removeLogicStrategy(LUBRICANT_CONSUMPTION_LOGIC_NAME)
    }

    override fun onUpgradeLoad(tile: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        val lubricantTank = FluidTileEntityComponent(capacity = 8000,
                allowedFluid = *arrayOf(heatTransferOilFluid.name),
                tanktype = DynamicFluidCapability.TankType.INPUT,
                facing = mutableSetOf(EnumFacing.NORTH))
        val lubricantMultiplier = MultiplierTileEntityComponent(null)

        // since loads are not only triggered when loading the chunk, but also upon packages from server, check
        // whether the component is already present
        if (tile.getComponents().none { it.first == LUBRICANT_FLUID_COMPONENT_NAME }) {
            tile.registerComponent(lubricantTank, LUBRICANT_FLUID_COMPONENT_NAME)
            tile.registerComponent(lubricantMultiplier, LUBRICANT_MULTIPLIER_COMPONENT_NAME)
            tile.addLogicStrategy(AdditiveConsumptionLogic(lubricantTank, LUBRICANT_BASE_USAGE, lubricantMultiplier),
                    LUBRICANT_CONSUMPTION_LOGIC_NAME)
        }
    }
}

class CoolerUpgrade : MachineUpgrade(MACHINE_UPGRADE_GENERIC) {
    companion object {
        const val COOLER_HEAT_STORAGE_COMPONENT_NAME = "special_cooler"
        const val COOLER_LOGIC = "special_cooler_logic"
        const val HEAT_GEN_LOGIC = "special_cooler_heating_logic"
        const val COOLER_FLUID_INPUT_NAME = "special_cooler_agent_input"
        const val COOLER_FLUID_OUTPUT_NAME = "special_cooler_agent_output"
    }

    override fun canInstallUpgrade(tile: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return upgrades
                .getInstalledUpgrades()
                .filterIsInstance<CoolerUpgrade>()
                .isEmpty() &&
                tile.getComponents()
                        .map { it.second }
                        .filterIsInstance<HeatStorageTileEntityComponent>()
                        .isEmpty() &&
                tile.getComponents()
                        .map { it.second }
                        .filterIsInstance<MultiplierTileEntityComponent>()
                        .filter { it.upgradeParameter == MACHINE_UPGRADE_ENERGY }
                        .count() == 1

    }

    override fun onInstallUpgrade(tile: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        this.onUpgradeLoad(tile, upgrades)
    }

    override fun onUninstallUpgrade(tile: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        tile.removeComponent(COOLER_HEAT_STORAGE_COMPONENT_NAME)
        tile.removeComponent(COOLER_FLUID_INPUT_NAME)
        tile.removeComponent(COOLER_FLUID_OUTPUT_NAME)
        tile.removeLogicStrategy(COOLER_LOGIC)
        tile.removeLogicStrategy(HEAT_GEN_LOGIC)
    }

    override fun onUpgradeLoad(tile: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        val heatStorage = HeatStorageTileEntityComponent(0, 100_000)
        val coolerAgentInput = FluidTileEntityComponent(
                capacity = 8000,
                facing = mutableSetOf(EnumFacing.NORTH),
                tanktype = DynamicFluidCapability.TankType.INPUT)
        val coolerAgentOutput = FluidTileEntityComponent(
                capacity = 8000,
                facing = mutableSetOf(EnumFacing.SOUTH),
                tanktype = DynamicFluidCapability.TankType.OUTPUT
        )

        if (tile.getComponents().none { it.first == COOLER_HEAT_STORAGE_COMPONENT_NAME }) {
            tile.registerComponent(heatStorage, COOLER_HEAT_STORAGE_COMPONENT_NAME)
            tile.registerComponent(coolerAgentInput, COOLER_FLUID_INPUT_NAME)
            tile.registerComponent(coolerAgentOutput, COOLER_FLUID_OUTPUT_NAME)
            tile.addLogicStrategy(CoolingLogic(
                    hotFluidComponent = coolerAgentOutput,
                    coldFluidComponent = coolerAgentInput,
                    heatBuffer = heatStorage), COOLER_LOGIC)
            tile.addLogicStrategy(GenerateHeatLogic(
                    baseHeatGeneration = TCFoundation.physics.milliHeatPerRf / 2,
                    energyMultiplierComponent = tile.getComponents()
                            .map { it.second }
                            .filterIsInstance<MultiplierTileEntityComponent>()
                            .single { it.upgradeParameter == MACHINE_UPGRADE_ENERGY },
                    heatBuffer = heatStorage), HEAT_GEN_LOGIC)
        }
    }

}