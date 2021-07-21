package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.content.items.UpgradeItem
import net.cydhra.technocracy.foundation.content.items.upgrades.EnergyUpgrade
import net.cydhra.technocracy.powertools.content.item.upgrades.*
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item

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
val bootsUpgradeFeatherFallItem = UpgradeItem("boots_upgrade_feather_fall", UpgradeClass.FEET, featherFallUpgrade)
val bootsUpgradePropulsionItem = UpgradeItem("boots_upgrade_propulsion", UpgradeClass.FEET, propulsionUpgrade)
val chestplateUpgradeJetpackItem = UpgradeItem("chestplate_upgrade_jetpack", UpgradeClass.HELMET, jetPackUpgrade)

val toolUpgradeShovel = UpgradeItem("tool_upgrade_shovel", UpgradeClass.TOOL,
        ToolClassUpgrade("shovel", Item.ToolMaterial.DIAMOND.harvestLevel))
val toolUpgradeAxe = UpgradeItem("tool_upgrade_axe", UpgradeClass.TOOL,
        ToolClassUpgrade("axe", Item.ToolMaterial.DIAMOND.harvestLevel))
val toolUpgradePickaxe = UpgradeItem("tool_upgrade_pickaxe", UpgradeClass.TOOL,
        ToolClassUpgrade("pickaxe", Item.ToolMaterial.DIAMOND.harvestLevel))

val modularItem = ModularItem()
val energyShield = EnergyShieldItem()
val modularhelmet = ModularArmor("modular_helmet", ModularArmor.armor, EntityEquipmentSlot.HEAD, UpgradeClass.HELMET)
val modularboots = ModularArmor("modular_boots", ModularArmor.armor, EntityEquipmentSlot.FEET, UpgradeClass.FEET)
val modularleggings = ModularArmor("modular_leggings", ModularArmor.armor, EntityEquipmentSlot.LEGS, UpgradeClass.LEGGINS)
val modularchestplate = ModularArmor("modular_chestplate", ModularArmor.armor, EntityEquipmentSlot.CHEST, UpgradeClass.CHESTPLATE)
