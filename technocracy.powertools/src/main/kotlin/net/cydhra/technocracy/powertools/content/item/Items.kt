package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.content.items.upgrades.EnergyUpgrade
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.powertools.content.item.upgrades.CapacityUpgrade

val batteryUpgrade_One = UpgradeItem("battery_upgrade_one", UpgradeClass.TOOL, EnergyUpgrade(400))
val batteryCapacityUpgrade = UpgradeItem("battery_capacity_upgrade", UpgradeClass.TOOL, CapacityUpgrade(1.2))
val modularItem = ModularItem()
val energyShield = EnergyShieldItem()
