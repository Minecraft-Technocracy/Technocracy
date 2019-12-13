package net.cydhra.technocracy.foundation.content.tileentities.upgrades

import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgrade
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeParameter
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MultiplierUpgrade

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
class LubricantUpgrade : MachineUpgrade(MACHINE_UPGRADE_GENERIC)