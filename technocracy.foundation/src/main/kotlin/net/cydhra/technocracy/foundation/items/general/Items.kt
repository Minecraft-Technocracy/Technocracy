package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.items.ItemPolishedMirror
import net.cydhra.technocracy.foundation.items.color.ConstantItemColor

val coalDustItem = ColoredItem("dust", "coal", ConstantItemColor(0x2f2f2f))

val machineFrameItem = BaseItem("machine_frame")
val sulfurItem = BaseItem("sulfur", oreDictName = "dustSulfur")
val batteryItem = BaseItem("battery")
val siliconItem = BaseItem("silicon", oreDictName = "itemSilicon")
val bedrockiumItem = BaseItem("bedrockium", oreDictName = "ingotBedrock")
val phenoplastItem = BaseItem("phenoplast")
val polypropyleneItem = BaseItem("polypropylene", oreDictName = "sheetPlastic")
val polyacrylateItem = BaseItem("polyacrylate")
val polystyreneItem = BaseItem("polystyrene")
val nanotubesItem = BaseItem("nanotubes")
val siliconChlorideItem = BaseItem("silicon_chloride", oreDictName = "dustSiliconChloride")
val saltItem = BaseItem("salt", oreDictName = "dustSalt")
val sodiumAcrylateItem = BaseItem("sodium_acrylate", oreDictName = "dustSodiumAcrylate")
val rubberItem = BaseItem("rubber", oreDictName = "itemRubber")
val calciumAcetateItem = BaseItem("calcium_acetate", oreDictName = "dustCalciumAcetate")
val mirrorItem = BaseItem("mirror")
val polishedMirrorItem = ItemPolishedMirror()

val invarItem = AlloyItem("invar", ConstantItemColor(0xDDC2BE))
val steelItem = AlloyItem("steel", ConstantItemColor(0x707680))
val bronzeItem = AlloyItem("bronze", ConstantItemColor(0xFFAA35))
val siliconBronzeItem = AlloyItem("siliconBronze", ConstantItemColor(0xCC9E44))
val superconductorItem = AlloyItem("superconductor", ConstantItemColor(0x7AA9DB))
val metallicPhaseChangeMaterialItem = AlloyItem("mPCM", ConstantItemColor(0xC9BEC9))
val lightAlloyItem = AlloyItem("lightAlloy", ConstantItemColor(0xE5E4E2))
val toughAlloyItem = AlloyItem("toughAlloy", ConstantItemColor(0x003030))

val pipeItem = PipeItem()
val facadeItem = FacadeItem()
val structureMarker = StructureMarkerItem()
val emptyCan = EmptyCanItem()