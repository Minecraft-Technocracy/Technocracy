package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.IronBeamBlock
import net.cydhra.technocracy.foundation.blocks.MachineBlock
import net.cydhra.technocracy.foundation.blocks.OilSand
import net.cydhra.technocracy.foundation.blocks.PipeBlock
import net.cydhra.technocracy.foundation.blocks.liquid.OilBlock
import net.cydhra.technocracy.foundation.tileentity.machines.*

val alloySmelteryBlock = MachineBlock("alloy_smeltery", ::TileEntityAlloySmeltery)
val centrifugeBlock = MachineBlock("centrifuge", ::TileEntityCentrifuge)
val chemicalEnrichmentChamberBlock = MachineBlock("chemical_enrichment_chamber", ::TileEntityChemicalEnrichmentChamber)
val chemicalEtchingChamberBlock = MachineBlock("chemical_etching_chamber", ::TileEntityChemicalEtchingChamber)
val chemicalOxidizerBlock = MachineBlock("chemical_oxidizer", ::TileEntityChemicalOxidizer)
val chemicalReactionChamberBlock = MachineBlock("chemical_reaction_chamber", ::TileEntityChemicalReactionChamber)
val compactorBlock = MachineBlock("compactor", ::TileEntityCompactor)
val electricFurnaceBlock = MachineBlock("electric_furnace", ::TileEntityElectricFurnace)
val electrolysisChamberBlock = MachineBlock("electrolysis_chamber", ::TileEntityElectrolysisChamber)
val kilnBlock = MachineBlock("kiln", ::TileEntityKiln)
val polymerizationChamberBlock = MachineBlock("polymerization_chamber", ::TileEntityPolymerizationChamber)
val pulverizerBlock = MachineBlock("pulverizer", ::TileEntityPulverizer)
val refineryBlock = MachineBlock("refinery", ::TileEntityRefinery)
val thermoelectricFreezerBlock = MachineBlock("thermoelectric_freezer", ::TileEntityThermoelectricFreezer)

val oilSandBlock = OilSand()
val ironBeamBlock = IronBeamBlock()

val oilBlock = OilBlock()

val pipe = PipeBlock()