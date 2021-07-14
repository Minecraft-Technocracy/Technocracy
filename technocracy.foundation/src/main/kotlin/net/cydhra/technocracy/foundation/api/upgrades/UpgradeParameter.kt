package net.cydhra.technocracy.foundation.api.upgrades

/**
 * A parameter of an upgradable entity, that can be modified by this upgrade. The upgradable entity must support this
 * parameter, otherwise the upgrade is ineffective and should not be installable within the upgradable entity. An
 * example of an upgradable parameter would be processing speed. If an upgrade does modify numeric parameters, but
 * adds complex behavior, the upgrade parameter should be "generic".
 */
typealias UpgradeParameter = String;

/**
 * Generic updates, that do not modify numeric parameters.
 */
const val UPGRADE_GENERIC: UpgradeParameter = "generic"

/**
 * Upgrades that modify speed
 */
const val UPGRADE_SPEED: UpgradeParameter = "speed"

/**
 * Upgrades that modify energy consumption
 */
const val UPGRADE_ENERGY: UpgradeParameter = "energy"

/**
 * Upgrades that modify additive consumption
 */
const val UPGRADE_ADDITIVE: UpgradeParameter = "additive"

/**
 * Upgrades that modify speeds that are not subject to a normal energy-speed tradeoff
 */
const val UPGRADE_CONVERSION: UpgradeParameter = "conversion"