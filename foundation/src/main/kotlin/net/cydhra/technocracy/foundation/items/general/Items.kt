package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.items.color.ConstantItemColor

val coalDustItem = ColoredItem("dust", "coal", ConstantItemColor(0x2f2f2f))
val ironDustItem = ColoredItem("dust", "iron", ConstantItemColor(0xD4D4CD))
val ironSheetItem = ColoredItem("sheet", "iron", ConstantItemColor(0xD4D4CD))

val machineFrameItem = BaseItem("machine_frame")
val batteryItem = BaseItem("battery")
val akkumulatorItem = BaseItem("akkumulator")
val siliconItem = BaseItem("silicon", oreDictName = "itemSilicon")
val bedrockiumItem = BaseItem("bedrockium", oreDictName = "ingotBedrock")
val phenoplastItem = BaseItem("phenoplast")
val polypropyleneItem = BaseItem("polypropylene", oreDictName = "sheetPlastic")
val polyacrylateItem = BaseItem("polyacrylate")
val polystyreneItem = BaseItem("polystyrene")

val invarItem = ColoredItem("ingot", "invar", ConstantItemColor(0xDDC2BE))
val siliconBronzeItem = ColoredItem("ingot", "silicon_bronze", ConstantItemColor(0xCC9E44))
val superconductorItem = ColoredItem("ingot", "superconductor", ConstantItemColor(0x7AA9DB))
val metallicPhaseChangeMaterialItem = ColoredItem("ingot", "mPCM", ConstantItemColor(0xC9BEC9))