package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.content.items.upgrades.EnergyUpgrade
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.powertools.content.item.upgrades.ArmorToughnessUpgrade
import net.cydhra.technocracy.powertools.content.item.upgrades.ArmorUpgrade
import net.cydhra.technocracy.powertools.content.item.upgrades.CapacityUpgrade

val batteryUpgrade_One = UpgradeItem("battery_upgrade_one", UpgradeClass.TOOL, EnergyUpgrade(800))
val batteryCapacityUpgrade = UpgradeItem("battery_capacity_upgrade", UpgradeClass.TOOL, CapacityUpgrade(1.2))
val armorUpgrade_IronPlating = UpgradeItem("armor_upgrade_steel_plating", UpgradeClass.TOOL, ArmorUpgrade(3.0),
        ArmorToughnessUpgrade(0.0))
val armorUpgrade_DiamondPlating = UpgradeItem("armor_upgrade_diamond_plating", UpgradeClass.TOOL, ArmorUpgrade(5.0),
        ArmorToughnessUpgrade(1.0))
val modularItem = ModularItem()
val energyShield = EnergyShieldItem()
val modularhelmet = ModularHelmet()
