package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.CoolingUpgrade
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.EnergyMultiplier
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.LubricantUpgrade
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.SpeedMultiplier
import net.cydhra.technocracy.foundation.model.items.api.AlloyItem
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.cydhra.technocracy.foundation.model.items.api.ColoredPrefixedItem
import net.cydhra.technocracy.foundation.model.items.api.UpgradeItem
import net.cydhra.technocracy.foundation.model.items.color.ConstantItemColor

val coalDustItem = ColoredPrefixedItem("dust", "coal", ConstantItemColor(0x2f2f2f))

val machineFrameItem = BaseItem("machine_frame")
val advancedMachineFrameItem = BaseItem("advanced_machine_frame")
val industrialMachineFrameItem = BaseItem("industrial_machine_frame")
val sulfurItem = BaseItem("sulfur", oreDictName = "dustSulfur")
val batteryItem = BaseItem("battery")
val siliconItem = BaseItem("silicon", oreDictName = "itemSilicon")
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
val bronzeSheetItem = ColoredPrefixedItem("sheet", "bronze", ConstantItemColor(0xFFAA35), false)
val steelGearItem = ColoredPrefixedItem("gear", "steel", ConstantItemColor(0x707680), false)
val steelGritItem = ColoredPrefixedItem("grit", "steel", ConstantItemColor(0x707680), false)

val membraneItem = BaseItem("membrane")
val ironRodItem = BaseItem("iron_rod", oreDictName = "rodIron")
val coilItem = BaseItem("coil")
val servoItem = BaseItem("servo")
val polyfibreItem = BaseItem("polyfibre")
val pumpItem = BaseItem("pump")
val fanItem = BaseItem("fan")
val spandexItem = BaseItem("spandex")
val cfrpItem = BaseItem("cfrp")

val pipeItem = PipeItem()
val facadeItem = FacadeItem()
val structureMarkerItem = StructureMarkerItem()
val emptyCanItem = EmptyCanItem()
val wrenchItem = WrenchItem()
val upgradeFrameItem = BaseItem("upgrade_frame")

val mechSpeedUp1Item = UpgradeItem("mech_speed_up_1", UpgradeClass.MACHINE,
        SpeedMultiplier(0.25), EnergyMultiplier(0.3))
val mechSpeedUp2Item = UpgradeItem("mech_speed_up_2", UpgradeClass.MACHINE,
        SpeedMultiplier(0.5), EnergyMultiplier(0.75))
val mechSpeedUp3Item = UpgradeItem("mech_speed_up_3", UpgradeClass.MACHINE,
        SpeedMultiplier(1.0), EnergyMultiplier(1.5))

val mechEnergyUp1Item = UpgradeItem("mech_energy_up_1", UpgradeClass.MACHINE,
        SpeedMultiplier(-0.4), EnergyMultiplier(-0.35))
val mechEnergyUp2Item = UpgradeItem("mech_energy_up_2", UpgradeClass.MACHINE,
        SpeedMultiplier(-0.7), EnergyMultiplier(-0.7))

val mechLubricantUpItem = UpgradeItem("mech_lubricant_up", UpgradeClass.MACHINE,
        SpeedMultiplier(2.0), LubricantUpgrade())
val elecCoolerUpgradeItem = UpgradeItem("elec_cooler_up", UpgradeClass.MACHINE,
        SpeedMultiplier(2.5), EnergyMultiplier(2.5), CoolingUpgrade())