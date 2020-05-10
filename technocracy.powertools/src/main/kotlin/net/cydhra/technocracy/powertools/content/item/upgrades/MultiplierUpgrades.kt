package net.cydhra.technocracy.powertools.content.item.upgrades

import net.cydhra.technocracy.foundation.model.items.api.upgrades.ItemMultiplierUpgrade


/**
 * Upgrades energy capacity of something
 */
class CapacityUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ENERGY_CAPACITY)

/**
 * Upgrades energy consumption of items. All logic clients that consume energy, should respect an energy consumption
 * multiplier
 */
class EnergyEfficiencyUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ENERGY_USAGE)

/**
 * Upgrades digging speed of tools
 */
class DigSpeedUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_DIG_SPEED)

/**
 * Upgrades armor of armor using a multiplier. (duh)
 */
class ArmorUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ARMOR)

/**
 * Upgrades a multiplier on walking speed of armor
 */
class WalkSpeedUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_WALK_SPEED)

/**
 * Upgrades an attack speed multiplier of an item
 */
class AttackSpeedUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ATTACK_SPEED)

/**
 * Upgrades an attack damage multiplier of an item
 */
class AttackDamageUpgrade(multiplier: Double) : ItemMultiplierUpgrade(multiplier, UPGRADE_ATTACK_DAMAGE)
