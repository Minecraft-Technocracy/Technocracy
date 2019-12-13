package net.cydhra.technocracy.foundation.content.tileentities.upgrades

import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgrade
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeParameter

const val MACHINE_UPGRADE_ENERGY: MachineUpgradeParameter = "energy"
const val MACHINE_UPGRADE_SPEED: MachineUpgradeParameter = "speed"
const val MACHINE_UPGRADE_ADDITIVE_CONSUMPTION: MachineUpgradeParameter = "additive"
const val MACHINE_UPGRADE_ACTIVE_COOLING: MachineUpgradeParameter = "cooling"

/**
 * A multiplier on machine energy consumption
 */
class EnergyMultiplier(val multiplier: Double) : MachineUpgrade(MACHINE_UPGRADE_ENERGY)

/**
 * A multiplier on machine processing speed
 */
class SpeedMultiplier(val multiplier: Double) : MachineUpgrade(MACHINE_UPGRADE_SPEED)

/**
 * A multiplier on how much of a machine's additive is consumed per tick of work.
 */
class AdditiveConsumptionMultiplier(val multiplier: Double) : MachineUpgrade(MACHINE_UPGRADE_ADDITIVE_CONSUMPTION)

/**
 * An upgrade that enables the machine to use coolants for speed increase
 */
class ActiveCoolingUpgrade : MachineUpgrade(MACHINE_UPGRADE_ACTIVE_COOLING)