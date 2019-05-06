package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.*
import net.cydhra.technocracy.foundation.blocks.liquid.OilBlock
import net.cydhra.technocracy.foundation.tileentity.machines.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPartBoiler
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerController
import net.minecraft.util.BlockRenderLayer

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

val boilerControllerBlock = MultiBlockActiveBlock("boiler_controller", ::TileEntityBoilerController)

val boilerWallBlock = PlainMultiBlockPartBlock("boiler_wall", ::TileEntityMultiBlockPartBoiler)
        .apply { setHardness(3.5f).setResistance(1f) }
val boilerGlassBlock = PlainMultiBlockPartBlock("boiler_glass", ::TileEntityMultiBlockPartBoiler,
        isFullCube = false, opaque = false, glassSides = true, renderLayer = BlockRenderLayer.CUTOUT)
        .apply { setHardness(1.5f).setResistance(1f) }

val oilSandBlock = OilSand()
val ironBeamBlock = IronBeamBlock()

val oilBlock = OilBlock()

val pipe = PipeBlock()