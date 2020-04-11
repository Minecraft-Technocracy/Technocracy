package net.cydhra.technocracy.foundation.content.tileentities.upgrades

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.tileentities.TCMachineTileEntity
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.fluids.diphenyletherFluid
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.HeatStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MultiplierTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.AdditiveConsumptionLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.CoolingLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.GenerateHeatLogic
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgrade
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.TileEntityMultiplierUpgrade
import net.cydhra.technocracy.foundation.model.upgrades.UpgradeParameter
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import java.util.*

const val MACHINE_UPGRADE_ENERGY: UpgradeParameter = "energy"
const val MACHINE_UPGRADE_SPEED: UpgradeParameter = "speed"
const val MACHINE_UPGRADE_ADDITIVE_CONSUMPTION: UpgradeParameter = "additive"

/**
 * This parameter type is used for all upgrades that do something to the machine but do not rely on a specific
 * machine parameter to be present. For example, [LubricantUpgrade] is a generic-parameter upgrade, as it can be
 * used on all machines per se.
 */
const val MACHINE_UPGRADE_GENERIC: UpgradeParameter = "generic"

/**
 * A multiplier on machine energy consumption
 */
class TEEnergyMultiplier(multiplier: Double) : TileEntityMultiplierUpgrade(multiplier, MACHINE_UPGRADE_ENERGY)

/**
 * A multiplier on machine processing speed
 */
class TESpeedMultiplier(multiplier: Double) : TileEntityMultiplierUpgrade(multiplier, MACHINE_UPGRADE_SPEED)

/**
 * A multiplier on how much of a machine's additive is consumed per tick of work.
 */
class TEAdditiveConsumptionMultiplier(multiplier: Double) : TileEntityMultiplierUpgrade(multiplier, MACHINE_UPGRADE_ADDITIVE_CONSUMPTION)

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

    override fun canInstallUpgrade(target: TCMachineTileEntity,
                                   upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return upgrades.getInstalledUpgrades()
                .filterIsInstance<LubricantUpgrade>()
                .isEmpty()
    }

    override fun onInstallUpgrade(target: TCMachineTileEntity,
                                  upgrades: MachineUpgradesTileEntityComponent) {
        this.onUpgradeLoad(target, upgrades)
    }

    override fun onUninstallUpgrade(target: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        target.removeComponent(LUBRICANT_FLUID_COMPONENT_NAME)
        target.removeComponent(LUBRICANT_MULTIPLIER_COMPONENT_NAME)
        target.removeLogicStrategy(LUBRICANT_CONSUMPTION_LOGIC_NAME)
    }

    override fun onUpgradeLoad(target: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        val lubricantTank = FluidTileEntityComponent(capacity = 8000,
                allowedFluid = *arrayOf(diphenyletherFluid.name),
                tanktype = DynamicFluidCapability.TankType.INPUT,
                facing = mutableSetOf(EnumFacing.NORTH))
        val lubricantMultiplier = MultiplierTileEntityComponent(null)

        // since loads are not only triggered when loading the chunk, but also upon packages from server, check
        // whether the component is already present
        if (target.getComponents().none { it.first == LUBRICANT_FLUID_COMPONENT_NAME }) {
            target.registerComponent(lubricantTank, LUBRICANT_FLUID_COMPONENT_NAME)
            target.registerComponent(lubricantMultiplier, LUBRICANT_MULTIPLIER_COMPONENT_NAME)
            target.addLogicStrategy(AdditiveConsumptionLogic(lubricantTank, LUBRICANT_BASE_USAGE, lubricantMultiplier),
                    LUBRICANT_CONSUMPTION_LOGIC_NAME)
        }
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.of(
                TextComponentTranslation("tooltips.upgrades.hint.lubricant")
                        .setStyle(Style().setColor(TextFormatting.GREEN))
        )
    }
}

class CoolingUpgrade : MachineUpgrade(MACHINE_UPGRADE_GENERIC) {
    companion object {
        const val COOLER_HEAT_STORAGE_COMPONENT_NAME = "special_cooler"
        const val COOLER_LOGIC = "special_cooler_logic"
        const val HEAT_GEN_LOGIC = "special_cooler_heating_logic"
        const val COOLER_FLUID_INPUT_NAME = "special_cooler_agent_input"
        const val COOLER_FLUID_OUTPUT_NAME = "special_cooler_agent_output"
    }

    override fun canInstallUpgrade(target: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return upgrades
                .getInstalledUpgrades()
                .filterIsInstance<CoolingUpgrade>()
                .isEmpty() &&
                target.getComponents()
                        .map { it.second }
                        .filterIsInstance<HeatStorageTileEntityComponent>()
                        .isEmpty() &&
                target.getComponents()
                        .map { it.second }
                        .filterIsInstance<MultiplierTileEntityComponent>()
                        .filter { it.upgradeParameter == MACHINE_UPGRADE_ENERGY }
                        .count() == 1

    }

    override fun onInstallUpgrade(target: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        this.onUpgradeLoad(target, upgrades)
    }

    override fun onUninstallUpgrade(target: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        target.removeComponent(COOLER_HEAT_STORAGE_COMPONENT_NAME)
        target.removeComponent(COOLER_FLUID_INPUT_NAME)
        target.removeComponent(COOLER_FLUID_OUTPUT_NAME)
        target.removeLogicStrategy(COOLER_LOGIC)
        target.removeLogicStrategy(HEAT_GEN_LOGIC)
    }

    override fun onUpgradeLoad(target: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
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

        if (target.getComponents().none { it.first == COOLER_HEAT_STORAGE_COMPONENT_NAME }) {
            target.registerComponent(heatStorage, COOLER_HEAT_STORAGE_COMPONENT_NAME)
            target.registerComponent(coolerAgentInput, COOLER_FLUID_INPUT_NAME)
            target.registerComponent(coolerAgentOutput, COOLER_FLUID_OUTPUT_NAME)
            target.addLogicStrategy(CoolingLogic(
                    hotFluidComponent = coolerAgentOutput,
                    coldFluidComponent = coolerAgentInput,
                    heatBuffer = heatStorage), COOLER_LOGIC)
            target.addLogicStrategy(GenerateHeatLogic(
                    baseHeatGeneration = TCFoundation.physics.milliHeatPerRf / 2,
                    energyMultiplierComponent = target.getComponents()
                            .map { it.second }
                            .filterIsInstance<MultiplierTileEntityComponent>()
                            .single { it.upgradeParameter == MACHINE_UPGRADE_ENERGY },
                    heatBuffer = heatStorage), HEAT_GEN_LOGIC)
        }
    }

    override fun getUpgradeDescription(): Optional<ITextComponent> {
        return Optional.of(
                TextComponentTranslation("tooltips.upgrades.hint.cooler")
                        .setStyle(Style().setColor(TextFormatting.GREEN))
        )
    }
}