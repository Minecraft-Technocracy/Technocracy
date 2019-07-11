package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.items.color.ConstantItemColor

val coalDustItem = ColoredItem("dust", "coal", ConstantItemColor(0x2f2f2f))
val ironDustItem = ColoredItem("dust", "iron", ConstantItemColor(0xD4D4CD))
val ironSheetItem = ColoredItem("sheet", "iron", ConstantItemColor(0xD4D4CD))

val machineFrameItem = BaseItem("machine_frame")
val sulfur = BaseItem("sulfur", oreDictName = "dustSulfur")
val batteryItem = BaseItem("battery")
val siliconItem = BaseItem("silicon", oreDictName = "itemSilicon")
val bedrockiumItem = BaseItem("bedrockium", oreDictName = "ingotBedrock")
val phenoplastItem = BaseItem("phenoplast")
val polypropyleneItem = BaseItem("polypropylene", oreDictName = "sheetPlastic")
val polyacrylateItem = BaseItem("polyacrylate")
val polystyreneItem = BaseItem("polystyrene")
val nanotubes = BaseItem("nanotubes")

val invarItem = AlloyItem("invar", ConstantItemColor(0xDDC2BE))
val siliconBronzeItem = AlloyItem("silicon_bronze", ConstantItemColor(0xCC9E44))
val superconductorItem = AlloyItem("superconductor", ConstantItemColor(0x7AA9DB))
val metallicPhaseChangeMaterialItem = AlloyItem("mPCM", ConstantItemColor(0xC9BEC9))

val pipeItem = PipeItem()
val facadeItem = FacadeItem()