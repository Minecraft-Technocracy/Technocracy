package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.content.items.upgrades.EnergyUpgrade
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.powertools.content.item.logic.FireExtinguishLogic
import net.cydhra.technocracy.powertools.content.item.upgrades.*
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting

val batteryUpgrade_One = UpgradeItem("battery_upgrade_one", UpgradeClass.TOOL, EnergyUpgrade(800))
val batteryCapacityUpgrade = UpgradeItem("battery_capacity_upgrade", UpgradeClass.TOOL, CapacityUpgrade(1.2))
val armorUpgrade_IronPlating = UpgradeItem("armor_upgrade_steel_plating", UpgradeClass.ARMOR, ArmorUpgrade(3.0),
        ArmorToughnessUpgrade(0.0))
val armorUpgrade_DiamondPlating = UpgradeItem("armor_upgrade_diamond_plating", UpgradeClass.ARMOR, ArmorUpgrade(5.0),
        ArmorToughnessUpgrade(1.0))

val chestplateUpgrade_FireResistance = UpgradeItem("chestplate_upgrade_fire_resistance", UpgradeClass.HELMET, fireExtinguishUpgrade)
val helmetUpgrade_WaterElectrolyzer = UpgradeItem("helmet_upgrade_water_electrolyzer", UpgradeClass.HELMET, waterElectrolyzerUpgrade)
val helmetUpgrade_NightVision = UpgradeItem("helmet_upgrade_night_vision", UpgradeClass.HELMET, nightVisionUpgrade)
val chestplateUpgrade_Jetpack = UpgradeItem("chestplate_upgrade_jetpack", UpgradeClass.HELMET, jetPackUpgrade)

val modularItem = ModularItem()
val energyShield = EnergyShieldItem()
val modularhelmet = ModularHelmet()
