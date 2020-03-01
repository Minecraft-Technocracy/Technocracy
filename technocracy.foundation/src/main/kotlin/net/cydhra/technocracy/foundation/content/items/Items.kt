package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.content.tileentities.upgrades.*
import net.cydhra.technocracy.foundation.model.items.api.AlloyItem
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.cydhra.technocracy.foundation.model.items.api.ColoredPrefixedItem
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.foundation.model.items.color.ConstantItemColor
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass

val coalDustItem = ColoredPrefixedItem("dust", "coal", ConstantItemColor(0x2f2f2f))

val machineFrameItem = BaseItem("machine_frame")
val advancedMachineFrameItem = BaseItem("advanced_machine_frame")
val industrialMachineFrameItem = BaseItem("industrial_machine_frame")
val sulfurItem = BaseItem("sulfur", oreDictName = "dustSulfur")
val batteryItem = BaseItem("battery")
val siliconItem = BaseItem("silicon", oreDictName = "itemSilicon")
val bedrockiumItem = BaseItem("bedrockium", oreDictName = "ingotBedrock")
val polypropyleneItem = BaseItem("polypropylene", oreDictName = "sheetPlastic")
val polyacrylateItem = BaseItem("polyacrylate")
val polystyreneItem = BaseItem("polystyrene")
val nanotubesItem = BaseItem("nanotubes")
val siliconChlorideItem = BaseItem("silicon_chloride", oreDictName = "dustSiliconChloride")
val saltItem = BaseItem("salt", oreDictName = "dustSalt")
val sodiumAcrylateItem = BaseItem("sodium_acrylate", oreDictName = "dustSodiumAcrylate")
val rubberItem = BaseItem("rubber", oreDictName = "itemRubber")
val mirrorItem = BaseItem("mirror")
val polishedMirrorItem = ItemPolishedMirror()
val circuitBoardItem = BaseItem("circuit_board")
val glueBallItem = BaseItem("glue_ball", oreDictName = "slimeball")
val asbestosItem = BaseItem("asbestos", oreDictName = "itemAsbestos")
val biphenylItem = BaseItem("biphenyl")

val invarItem = AlloyItem("invar", ConstantItemColor(0xDDC2BE))
val steelItem = AlloyItem("steel", ConstantItemColor(0x707680))
val bronzeItem = AlloyItem("bronze", ConstantItemColor(0xFFAA35))
val electrumItem = AlloyItem("electrum", ConstantItemColor(0xFFDF00))
val siliconBronzeItem = AlloyItem("siliconBronze", ConstantItemColor(0xCC9E44))
val superconductorItem = AlloyItem("superconductor", ConstantItemColor(0x7AA9DB))
val metallicPhaseChangeMaterialItem = AlloyItem("mPCM", ConstantItemColor(0xC9BEC9))
val lightAlloyItem = AlloyItem("lightAlloy", ConstantItemColor(0xE5E4E2))
val toughAlloyItem = AlloyItem("zircalloy", ConstantItemColor(0x003030))

val invarSheetItem = ColoredPrefixedItem("sheet", "invar", ConstantItemColor(0xDDC2BE), false)
val steelSheetItem = ColoredPrefixedItem("sheet", "steel", ConstantItemColor(0x707680), false)

val steelGearItem = ColoredPrefixedItem("gear", "steel", ConstantItemColor(0x707680), false)

val membraneItem = BaseItem("membrane")
val ironRodItem = BaseItem("iron_rod")
val coilItem = BaseItem("coil")
val servoItem = BaseItem("servo")
val polyfibreItem = BaseItem("polyfibre")
val pumpItem = BaseItem("pump")
val fanItem = BaseItem("fan")

val pipeItem = PipeItem()
val facadeItem = FacadeItem()
val structureMarkerItem = StructureMarkerItem()
val emptyCanItem = EmptyCanItem()
val wrenchItem = WrenchItem()
val upgradeFrameItem = BaseItem("upgrade_frame")

val mechSpeedUp1Item = UpgradeItem("mech_speed_up_1", MachineUpgradeClass.MECHANICAL,
        SpeedMultiplier(0.25), EnergyMultiplier(0.3))
val mechSpeedUp2Item = UpgradeItem("mech_speed_up_2", MachineUpgradeClass.MECHANICAL,
        SpeedMultiplier(0.5), EnergyMultiplier(0.75))
val mechSpeedUp3Item = UpgradeItem("mech_speed_up_3", MachineUpgradeClass.MECHANICAL,
        SpeedMultiplier(1.0), EnergyMultiplier(1.5))

val mechEnergyUp1Item = UpgradeItem("mech_energy_up_1", MachineUpgradeClass.MECHANICAL,
        SpeedMultiplier(-0.4), EnergyMultiplier(-0.35))
val mechEnergyUp2Item = UpgradeItem("mech_energy_up_2", MachineUpgradeClass.MECHANICAL,
        SpeedMultiplier(-0.7), EnergyMultiplier(-0.7))

val chemSpeedUp1Item = UpgradeItem("chem_speed_up_1", MachineUpgradeClass.CHEMICAL,
        SpeedMultiplier(0.25), EnergyMultiplier(0.3))
val chemSpeedUp2Item = UpgradeItem("chem_speed_up_2", MachineUpgradeClass.CHEMICAL,
        SpeedMultiplier(0.5), EnergyMultiplier(0.75))
val chemSpeedUp3Item = UpgradeItem("chem_speed_up_3", MachineUpgradeClass.CHEMICAL,
        SpeedMultiplier(1.0), EnergyMultiplier(1.5))
val chemSpeedUp4Item = UpgradeItem("chem_speed_up_4", MachineUpgradeClass.CHEMICAL,
        SpeedMultiplier(-0.5), EnergyMultiplier(-0.3), AdditiveConsumptionMultiplier(-0.3))

val thermSpeedUp1Item = UpgradeItem("therm_speed_up_1", MachineUpgradeClass.THERMAL,
        SpeedMultiplier(0.25), EnergyMultiplier(0.3))
val thermSpeedUp2Item = UpgradeItem("therm_speed_up_2", MachineUpgradeClass.THERMAL,
        SpeedMultiplier(0.5), EnergyMultiplier(0.8))
val thermSpeedUp3Item = UpgradeItem("therm_speed_up_3", MachineUpgradeClass.THERMAL,
        SpeedMultiplier(1.0), EnergyMultiplier(1.8))

val thermEnergyUp1Item = UpgradeItem("therm_energy_up_1", MachineUpgradeClass.THERMAL,
        SpeedMultiplier(-0.3), EnergyMultiplier(-0.4))
val thermEnergyUp2Item = UpgradeItem("therm_energy_up_2", MachineUpgradeClass.THERMAL,
        EnergyMultiplier(-0.8), AdditiveConsumptionMultiplier(0.8))

val elecSpeedUp1Item = UpgradeItem("elec_speed_up_1", MachineUpgradeClass.ELECTRICAL,
        SpeedMultiplier(0.25), EnergyMultiplier(0.3))
val elecSpeedUp2Item = UpgradeItem("elec_speed_up_2", MachineUpgradeClass.ELECTRICAL,
        SpeedMultiplier(0.5), EnergyMultiplier(0.75))
val elecSpeedUp3Item = UpgradeItem("elec_speed_up_3", MachineUpgradeClass.ELECTRICAL,
        SpeedMultiplier(1.0), EnergyMultiplier(1.6))

val elecEnergyUp1Item = UpgradeItem("elec_energy_up_1", MachineUpgradeClass.ELECTRICAL,
        SpeedMultiplier(-0.1), EnergyMultiplier(-0.2))
val elecEnergyUp2Item = UpgradeItem("elec_energy_up_2", MachineUpgradeClass.ELECTRICAL,
        SpeedMultiplier(-0.2), EnergyMultiplier(-0.5))

val compSpeedUp1Item = UpgradeItem("comp_speed_up_1", MachineUpgradeClass.COMPUTER,
        SpeedMultiplier(0.25), EnergyMultiplier(0.4))
val compSpeedUp2Item = UpgradeItem("comp_speed_up_2", MachineUpgradeClass.COMPUTER,
        SpeedMultiplier(0.5), EnergyMultiplier(0.9))
val compSpeedUp3Item = UpgradeItem("comp_speed_up_3", MachineUpgradeClass.COMPUTER,
        SpeedMultiplier(1.1), EnergyMultiplier(1.4))
val compSpeedUp4Item = UpgradeItem("comp_speed_up_4", MachineUpgradeClass.COMPUTER,
        SpeedMultiplier(1.9), EnergyMultiplier(2.5))

val optSpeedUp1Item = UpgradeItem("opt_speed_up_1", MachineUpgradeClass.OPTICAL,
        SpeedMultiplier(0.25), EnergyMultiplier(0.3))
val optSpeedUp2Item = UpgradeItem("opt_speed_up_2", MachineUpgradeClass.OPTICAL,
        SpeedMultiplier(0.5), EnergyMultiplier(0.65))
val optSpeedUp3Item = UpgradeItem("opt_speed_up_3", MachineUpgradeClass.OPTICAL,
        SpeedMultiplier(1.0), EnergyMultiplier(1.4))

val nucSpeedUp1Item = UpgradeItem("nuc_speed_up_1", MachineUpgradeClass.NUCLEAR,
        SpeedMultiplier(0.25), EnergyMultiplier(0.25))
val nucSpeedUp2Item = UpgradeItem("nuc_speed_up_2", MachineUpgradeClass.NUCLEAR,
        SpeedMultiplier(0.5), EnergyMultiplier(0.8))
val nucSpeedUp3Item = UpgradeItem("nuc_speed_up_3", MachineUpgradeClass.NUCLEAR,
        SpeedMultiplier(1.0), EnergyMultiplier(2.2))

val mechLubricantUpItem = UpgradeItem("mech_lubricant_up", MachineUpgradeClass.MECHANICAL,
        SpeedMultiplier(2.0), LubricantUpgrade())
val elecCoolerUpgradeItem = UpgradeItem("elec_cooler_up", MachineUpgradeClass.ELECTRICAL,
        SpeedMultiplier(2.5), EnergyMultiplier(2.5), CoolerUpgrade())