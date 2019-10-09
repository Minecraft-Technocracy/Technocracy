package net.cydhra.technocracy.astronautics.blocks.general

import net.cydhra.technocracy.astronautics.blocks.*
import net.cydhra.technocracy.astronautics.client.astronauticsColorTabs
import net.cydhra.technocracy.foundation.model.blocks.impl.ColoredPlainBlock
import net.cydhra.technocracy.foundation.model.blocks.impl.PlainBlock
import net.minecraft.block.material.Material

val scaffoldBlock = ScaffoldBlock()
val reinforcedConcreteBlock = ReinforcedConcreteBlock()
val wetConcreteBlock = WetConcreteBlock()
val wetReinforcedConcreteBlock = WetReinforcedConcreteBlock()
val rocketControllerBlock = RocketControllerBlock()

val rocketHullBlock = ColoredPlainBlock("rocket_hull", Material.IRON, colorTab = astronauticsColorTabs)
val rocketDriveBlock = PlainBlock("rocket_drive", Material.IRON)
val rocketTipBlock = PlainBlock("rocket_tip", Material.IRON)
val rocketStorageBlock = ColoredPlainBlock("rocket_storage", Material.IRON, colorTab = astronauticsColorTabs)
val rocketTank = ColoredPlainBlock("rocket_tank", Material.IRON, colorTab = astronauticsColorTabs)
