package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.items.color.ConstantItemColor
import net.minecraft.creativetab.CreativeTabs

val coalDustItem = ColoredItem("dust", "coal", ConstantItemColor(0x2f2f2f))
val ironDustItem = ColoredItem("dust", "iron", ConstantItemColor(0xD4D4CD))
val ironSheetItem = ColoredItem("sheet", "iron", ConstantItemColor(0xD4D4CD))

val machineFrameItem = BaseItem("machine_frame").apply { creativeTab = CreativeTabs.DECORATIONS }
val batteryItem = BaseItem("battery").apply { creativeTab = CreativeTabs.MATERIALS }
val akkumulatorItem = BaseItem("akkumulator").apply { creativeTab = CreativeTabs.MATERIALS }