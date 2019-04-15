package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.IronBeamBlock
import net.cydhra.technocracy.foundation.blocks.MachineBlock
import net.cydhra.technocracy.foundation.blocks.OilSand
import net.cydhra.technocracy.foundation.blocks.material.oilMaterial
import net.cydhra.technocracy.foundation.liquids.general.oilFluid
import net.cydhra.technocracy.foundation.tileentity.TileEntityCentrifuge
import net.cydhra.technocracy.foundation.tileentity.TileEntityElectricFurnace
import net.cydhra.technocracy.foundation.tileentity.TileEntityPulverizer

val pulverizerBlock = MachineBlock("pulverizer", ::TileEntityPulverizer)
val electricFurnaceBlock = MachineBlock("electric_furnace", ::TileEntityElectricFurnace)
val centrifugeBlock = MachineBlock("centrifuge", ::TileEntityCentrifuge)
val oilSandBlock = OilSand()
val ironBeamBlock = IronBeamBlock()
val oilBlock = BaseLiquidBlock(oilFluid, "oil", oilMaterial)