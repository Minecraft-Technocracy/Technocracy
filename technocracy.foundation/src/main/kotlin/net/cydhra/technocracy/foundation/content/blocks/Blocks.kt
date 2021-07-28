package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.content.tileentities.machines.*
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPartCapacitor
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPartRefinery
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPartSaline
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.capacitor.TileEntityCapacitorController
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.capacitor.TileEntityCapacitorEnergyPort
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery.TileEntityRefineryController
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery.TileEntityRefineryHeater
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery.TileEntityRefineryInput
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery.TileEntityRefineryOutput
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.saline.*
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.tank.TileEntityTankMultiBlockPart
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.tank.TileEntityTankPort
import net.cydhra.technocracy.foundation.content.tileentities.storage.TileEntityBattery
import net.minecraft.block.material.Material
import net.minecraft.util.BlockRenderLayer

val alloySmelteryBlock = MachineBlock("alloy_smeltery", ::TileEntityAlloySmeltery)
val centrifugeBlock = MachineBlock("centrifuge", ::TileEntityCentrifuge)
val chemicalProcessingChamberBlock = MachineBlock("chemical_processing_chamber", ::TileEntityChemicalProcessingChamber)
val chemicalEtchingChamberBlock = MachineBlock("chemical_etching_chamber", ::TileEntityChemicalEtchingChamber)
val chemicalOxidizerBlock = MachineBlock("chemical_oxidizer", ::TileEntityChemicalOxidizer)
val chemicalReactionChamberBlock = MachineBlock("chemical_reaction_chamber", ::TileEntityChemicalReactionChamber)
val compactorBlock = MachineBlock("compactor", ::TileEntityCompactor)
val crystallizationChamberBlock = MachineBlock("crystallization_chamber", ::TileEntityCrystallizationChamber)
val dissolutionChamberBlock = MachineBlock("dissolution_chamber", ::TileEntityDissolutionChamber)
val electricFurnaceBlock = MachineBlock("electric_furnace", ::TileEntityElectricFurnace)
val electrolysisChamberBlock = MachineBlock(
        "electrolysis_chamber",
        ::TileEntityElectrolysisChamber
).apply { renderLayers.add(BlockRenderLayer.TRANSLUCENT) }
val flowHeaterBlock = MachineBlock("flow_heater", ::TileEntityFlowHeater)
val industrialRefineryBlock = MachineBlock("industrial_refinery", ::TileEntityIndustrialRefinery)
val kilnBlock = MachineBlock("kiln", ::TileEntityKiln)
val polymerizationChamberBlock = MachineBlock("polymerization_chamber", ::TileEntityPolymerizationChamber)
val pulverizerBlock = MachineBlock("pulverizer", ::TileEntityPulverizer)

val refineryControllerBlock = MultiBlockRotatableActiveBlock("refinery_controller", ::TileEntityRefineryController)
val refineryWallBlock = PlainMultiBlockPartBlock("refinery_wall", ::TileEntityMultiBlockPartRefinery)
val refineryInputBlock = MultiBlockRotatableActiveBlock("refinery_input", ::TileEntityRefineryInput,
        renderLayer = BlockRenderLayer.CUTOUT)
val refineryOutputBlock = MultiBlockRotatableActiveBlock("refinery_output", ::TileEntityRefineryOutput,
        renderLayer = BlockRenderLayer.CUTOUT)
val refineryHeaterBlock = MultiBlockActiveBlock("refinery_heater", ::TileEntityRefineryHeater,
        renderLayer = BlockRenderLayer.CUTOUT)

val capacitorControllerBlock = MultiBlockRotatableActiveBlock("capacitor_controller", ::TileEntityCapacitorController)
val capacitorWallBlock = PlainMultiBlockPartBlock("capacitor_wall", ::TileEntityMultiBlockPartCapacitor)
val capacitorConnectorBlock = PlainMultiBlockPartBlock("capacitor_connector", ::TileEntityMultiBlockPartCapacitor)
val capacitorEnergyPortBlock = MultiBlockActiveBlock(
        "capacitor_energy_port", ::TileEntityCapacitorEnergyPort,
        renderLayer = BlockRenderLayer.CUTOUT
)

val tankWallBlock = TankStructureBlock("tank_wall", ::TileEntityTankMultiBlockPart)
val tankGlassBlock = TankStructureBlock(
        "tank_glass",
        ::TileEntityTankMultiBlockPart,
        glassSides = true,
        renderLayer = BlockRenderLayer.CUTOUT
).apply {
        isFullCube = false
        opaque = false
}
val tankIOBlock = TankStructureBlock("tank_io", ::TileEntityTankPort)

val batteryBlock = MachineBlock("battery_block", ::TileEntityBattery)

val salineControllerBlock = MultiBlockRotatableActiveBlock("saline_controller", ::TileEntitySalineController)
val salineWallBlock = PlainMultiBlockPartBlock("saline_wall", ::TileEntityMultiBlockPartSaline)
val salineHeatedWallBlock = PlainMultiBlockPartBlock("saline_heated_wall", ::TileEntityMultiBlockPartSaline)
val salineFluidInputBlock = MultiBlockRotatableActiveBlock(
        "saline_fluid_input", ::TileEntitySalineFluidInput,
        renderLayer = BlockRenderLayer.CUTOUT
)
val salineFluidOutputBlock = MultiBlockRotatableActiveBlock(
        "saline_fluid_output", ::TileEntitySalineFluidOutput,
        renderLayer = BlockRenderLayer.CUTOUT
)
val salineHeatingAgentInputBlock = MultiBlockRotatableActiveBlock(
        "saline_heating_agent_input", ::TileEntitySalineHeatingAgentInput,
        renderLayer = BlockRenderLayer.CUTOUT
)
val salineHeatingAgentOutputBlock = MultiBlockRotatableActiveBlock("saline_heating_agent_output", ::TileEntitySalineHeatingAgentOutput,
        renderLayer = BlockRenderLayer.CUTOUT)

val leadBlock = PlainBlock("lead_block", Material.IRON, oreDictionaryName = "blockLead")
val leadOxideBlock = PlainBlock("lead_oxide_block", Material.IRON, oreDictionaryName = "blockLeadOxide")

val oilSandBlock = OilSandBlock()
val oilStone = OilStoneBlock()

val oilBlock = OilBlock()
val sulfuricAcidBlock = SulfuricAcidBlock()

val pipe = PipeBlock()
val drum = DrumBlock()
val leadGlassPaneBlock = LeadGlassPaneBlock()
        .apply { setHardness(1.5f).setResistance(1f) }

val saltBlock = PlainBlock("salt_block", Material.ROCK, oreDictionaryName = "blockSalt")
        .apply { setHardness(1.0f).setResistance(2.5f) }
