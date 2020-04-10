package net.cydhra.technocracy.foundation.content.tileentities.upgrades

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.tileentities.TCMachineTileEntity
import net.cydhra.technocracy.foundation.api.upgrades.*
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.fluids.diphenyletherFluid
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.HeatStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MultiplierTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.AdditiveConsumptionLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.CoolingLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.GenerateHeatLogic
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MultiplierUpgrade
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import java.util.*

/**
 * A multiplier on machine energy consumption
 */
class EnergyMultiplier(multiplier: Double) : MultiplierUpgrade(multiplier, UPGRADE_ENERGY)

/**
 * A multiplier on machine processing speed
 */
class SpeedMultiplier(multiplier: Double) : MultiplierUpgrade(multiplier, UPGRADE_SPEED)

/**
 * A multiplier on how much of a machine's additive is consumed per tick of work.
 */
class AdditiveConsumptionMultiplier(multiplier: Double) : MultiplierUpgrade(multiplier, UPGRADE_ADDITIVE)

/**
 * An upgrade that enables the machine to use lubricant additives for speed increase.
 */
class LubricantUpgrade : Upgrade<TCMachineTileEntity> {

    companion object {
        const val LUBRICANT_FLUID_COMPONENT_NAME = "special_lubricant"
        const val LUBRICANT_MULTIPLIER_COMPONENT_NAME = "special_lubricant_multiplier"
        const val LUBRICANT_CONSUMPTION_LOGIC_NAME = "special_lubricant_consumption"
        const val LUBRICANT_BASE_USAGE = 4
    }

    override val upgradeParameter = UPGRADE_GENERIC

    override fun canInstallUpgrade(upgradable: TCMachineTileEntity,
                                   upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return upgrades.getInstalledUpgrades()
                .filterIsInstance<LubricantUpgrade>()
                .isEmpty()
    }

    override fun onInstallUpgrade(upgradable: TCMachineTileEntity,
                                  upgrades: MachineUpgradesTileEntityComponent) {
        this.onUpgradeLoad(upgradable, upgrades)
    }

    override fun onUninstallUpgrade(upgradable: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        upgradable.removeComponent(LUBRICANT_FLUID_COMPONENT_NAME)
        upgradable.removeComponent(LUBRICANT_MULTIPLIER_COMPONENT_NAME)
        upgradable.removeLogicStrategy(LUBRICANT_CONSUMPTION_LOGIC_NAME)
    }

    override fun onUpgradeLoad(upgradable: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        val lubricantTank = FluidTileEntityComponent(capacity = 8000,
                allowedFluid = *arrayOf(diphenyletherFluid.name),
                tanktype = DynamicFluidCapability.TankType.INPUT,
                facing = mutableSetOf(EnumFacing.NORTH))
        val lubricantMultiplier = MultiplierTileEntityComponent(null)

        // since loads are not only triggered when loading the chunk, but also upon packages from server, check
        // whether the component is already present
        if (upgradable.getComponents().none { it.first == LUBRICANT_FLUID_COMPONENT_NAME }) {
            upgradable.registerComponent(lubricantTank, LUBRICANT_FLUID_COMPONENT_NAME)
            upgradable.registerComponent(lubricantMultiplier, LUBRICANT_MULTIPLIER_COMPONENT_NAME)
            upgradable.addLogicStrategy(AdditiveConsumptionLogic(lubricantTank, LUBRICANT_BASE_USAGE, lubricantMultiplier),
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

class CoolingUpgrade : Upgrade<TCMachineTileEntity> {
    companion object {
        const val COOLER_HEAT_STORAGE_COMPONENT_NAME = "special_cooler"
        const val COOLER_LOGIC = "special_cooler_logic"
        const val HEAT_GEN_LOGIC = "special_cooler_heating_logic"
        const val COOLER_FLUID_INPUT_NAME = "special_cooler_agent_input"
        const val COOLER_FLUID_OUTPUT_NAME = "special_cooler_agent_output"
    }

    override val upgradeParameter = UPGRADE_GENERIC

    override fun canInstallUpgrade(upgradable: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent): Boolean {
        return upgrades
                .getInstalledUpgrades()
                .filterIsInstance<CoolingUpgrade>()
                .isEmpty() &&
                upgradable.getComponents()
                        .map { it.second }
                        .filterIsInstance<HeatStorageTileEntityComponent>()
                        .isEmpty() &&
                upgradable.getComponents()
                        .map { it.second }
                        .filterIsInstance<MultiplierTileEntityComponent>()
                        .filter { it.upgradeParameter == UPGRADE_ENERGY }
                        .count() == 1

    }

    override fun onInstallUpgrade(upgradable: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        this.onUpgradeLoad(upgradable, upgrades)
    }

    override fun onUninstallUpgrade(upgradable: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
        upgradable.removeComponent(COOLER_HEAT_STORAGE_COMPONENT_NAME)
        upgradable.removeComponent(COOLER_FLUID_INPUT_NAME)
        upgradable.removeComponent(COOLER_FLUID_OUTPUT_NAME)
        upgradable.removeLogicStrategy(COOLER_LOGIC)
        upgradable.removeLogicStrategy(HEAT_GEN_LOGIC)
    }

    override fun onUpgradeLoad(upgradable: TCMachineTileEntity, upgrades: MachineUpgradesTileEntityComponent) {
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

        if (upgradable.getComponents().none { it.first == COOLER_HEAT_STORAGE_COMPONENT_NAME }) {
            upgradable.registerComponent(heatStorage, COOLER_HEAT_STORAGE_COMPONENT_NAME)
            upgradable.registerComponent(coolerAgentInput, COOLER_FLUID_INPUT_NAME)
            upgradable.registerComponent(coolerAgentOutput, COOLER_FLUID_OUTPUT_NAME)
            upgradable.addLogicStrategy(CoolingLogic(
                    hotFluidComponent = coolerAgentOutput,
                    coldFluidComponent = coolerAgentInput,
                    heatBuffer = heatStorage), COOLER_LOGIC)
            upgradable.addLogicStrategy(GenerateHeatLogic(
                    baseHeatGeneration = TCFoundation.physics.milliHeatPerRf / 2,
                    energyMultiplierComponent = upgradable.getComponents()
                            .map { it.second }
                            .filterIsInstance<MultiplierTileEntityComponent>()
                            .single { it.upgradeParameter == UPGRADE_ENERGY },
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