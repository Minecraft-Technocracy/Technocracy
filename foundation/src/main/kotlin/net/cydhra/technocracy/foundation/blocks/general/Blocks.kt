package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.*
import net.cydhra.technocracy.foundation.blocks.material.oilMaterial
import net.cydhra.technocracy.foundation.liquids.general.oilFluid

val pulverizerBlock = Pulverizer()
val electricFurnaceBlock = ElectricFurnaceBlock()
val centrifugeBlock = CentrifugeBlock()
val oilSandBlock = OilSand()
val ironBeamBlock = IronBeamBlock()
val oilBlock = BaseLiquidBlock(oilFluid, "oil", oilMaterial)