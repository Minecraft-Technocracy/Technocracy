package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.content.items.upgrades.EnergyUpgrade
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.powertools.content.item.upgrades.*

val batteryUpgrade_One = UpgradeItem("battery_upgrade_one", UpgradeClass.TOOL, EnergyUpgrade(800))
val batteryCapacityUpgrade = UpgradeItem("battery_capacity_upgrade", UpgradeClass.TOOL, CapacityUpgrade(1.2))
val armorUpgrade_IronPlating = UpgradeItem("armor_upgrade_steel_plating", UpgradeClass.ARMOR, ArmorUpgrade(3.0),
        ArmorToughnessUpgrade(0.0))
val armorUpgrade_DiamondPlating = UpgradeItem("armor_upgrade_diamond_plating", UpgradeClass.ARMOR, ArmorUpgrade(5.0),
        ArmorToughnessUpgrade(1.0))

val chestplateUpgradeFireResistance = UpgradeItem("chestplate_upgrade_fire_resistance", UpgradeClass.HELMET, fireExtinguishUpgrade)
val helmetUpgradeWaterBreathing = UpgradeItem("helmet_upgrade_water_breathing", UpgradeClass.HELMET, waterBreathingUpgrade)
val helmetUpgradeNightVision = UpgradeItem("helmet_upgrade_night_vision", UpgradeClass.HELMET, nightVisionUpgrade)
val helmetUpgradeAquaAffinityItem = UpgradeItem("helmet_upgrade_aqua_affinity", UpgradeClass.HELMET, aquaAffinityUpgrade)
val bootsUpgradeFeatherFallItem = UpgradeItem("helmet_upgrade_feather_fall", UpgradeClass.HELMET, featherFallUpgrade)
val chestplateUpgrade_Jetpack = UpgradeItem("chestplate_upgrade_jetpack", UpgradeClass.HELMET, jetPackUpgrade)
val modularItem = ModularItem()
val energyShield = EnergyShieldItem()
val modularhelmet = ModularHelmet()
