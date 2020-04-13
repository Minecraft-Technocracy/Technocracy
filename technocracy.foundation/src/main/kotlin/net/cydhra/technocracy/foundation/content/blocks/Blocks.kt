package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.content.tileentities.machines.*
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.*
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.boiler.TileEntityBoilerController
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.boiler.TileEntityBoilerHeater
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.boiler.TileEntityBoilerInput
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.boiler.TileEntityBoilerOutput
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.capacitor.TileEntityCapacitorController
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.capacitor.TileEntityCapacitorEnergyPort
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.heatexchanger.TileEntityHeatExchangerController
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.heatexchanger.TileEntityHeatExchangerInput
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.heatexchanger.TileEntityHeatExchangerOutput
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery.TileEntityRefineryController
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery.TileEntityRefineryHeater
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery.TileEntityRefineryInput
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.refinery.TileEntityRefineryOutput
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.saline.*
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.tank.TileEntityTankMultiBlockPart
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.tank.TileEntityTankPort
import net.cydhra.technocracy.foundation.model.blocks.impl.*
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
val electrolysisChamberBlock = MachineBlock("electrolysis_chamber", ::TileEntityElectrolysisChamber).apply { renderLayers.add(BlockRenderLayer.TRANSLUCENT) }
val industrialRefineryBlock = MachineBlock("industrial_refinery", ::TileEntityIndustrialRefinery)
val kilnBlock = MachineBlock("kiln", ::TileEntityKiln)
val polymerizationChamberBlock = MachineBlock("polymerization_chamber", ::TileEntityPolymerizationChamber)
val pulverizerBlock = MachineBlock("pulverizer", ::TileEntityPulverizer)

val boilerControllerBlock = MultiBlockRotatableActiveBlock("boiler_controller", ::TileEntityBoilerController)
val boilerHeaterBlock = MultiBlockActiveBlock("boiler_heater", ::TileEntityBoilerHeater, renderLayer = BlockRenderLayer.CUTOUT)
val boilerFluidOutputBlock = MultiBlockActiveBlock("boiler_output", ::TileEntityBoilerOutput, renderLayer = BlockRenderLayer.CUTOUT)
val boilerFluidInputBlock = MultiBlockRotatableActiveBlock("boiler_input", ::TileEntityBoilerInput, BlockRenderLayer.CUTOUT)
val boilerWallBlock = PlainMultiBlockPartBlock("boiler_wall", ::TileEntityMultiBlockPartBoiler)
        .apply { setHardness(3.5f).setResistance(1f) }
val boilerGlassBlock = PlainMultiBlockPartBlock("boiler_glass", ::TileEntityMultiBlockPartBoiler,
        isFullCube = false, opaque = false, glassSides = true, renderLayer = BlockRenderLayer.CUTOUT)
        .apply { setHardness(1.5f).setResistance(1f) }
val boilerConductorBlock = PlainBlock("boiler_conductor", material = Material.IRON)
        .apply { setHardness(4f).setResistance(2.5f) }

val heatExchangerControllerBlock = MultiBlockRotatableActiveBlock("heat_exchanger_controller",
        ::TileEntityHeatExchangerController)
val heatExchangerWallBlock = PlainMultiBlockPartBlock("heat_exchanger_wall", ::TileEntityMultiBlockPartHeatExchanger)
val heatExchangerGlassBlock = PlainMultiBlockPartBlock("heat_exchanger_glass", ::TileEntityMultiBlockPartHeatExchanger,
        isFullCube = false, opaque = false, glassSides = true, renderLayer = BlockRenderLayer.CUTOUT)
        .apply { setHardness(1.5f).setResistance(1f) }
val heatExchangerColdAgentTube = PlainMultiBlockPartBlock("heat_exchanger_cold_tube", ::TileEntityMultiBlockPartHeatExchanger)
val heatExchangerHotAgentTube = PlainMultiBlockPartBlock("heat_exchanger_hot_tube", ::TileEntityMultiBlockPartHeatExchanger)
val heatExchangerInputBlock = MultiBlockRotatableActiveBlock("heat_exchanger_input", ::TileEntityHeatExchangerInput,
        renderLayer = BlockRenderLayer.CUTOUT)
val heatExchangerOutputBlock = MultiBlockRotatableActiveBlock("heat_exchanger_output", ::TileEntityHeatExchangerOutput,
        renderLayer = BlockRenderLayer.CUTOUT)

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
val capacitorEnergyPortBlock = MultiBlockActiveBlock("capacitor_energy_port", ::TileEntityCapacitorEnergyPort,
        renderLayer = BlockRenderLayer.CUTOUT)

val tankWallBlock = TankStructureBlock("tank_wall", ::TileEntityTankMultiBlockPart)
val tankGlassBlock = TankStructureBlock("tank_glass", ::TileEntityTankMultiBlockPart, isFullCube = false, opaque = false, glassSides = true, renderLayer = BlockRenderLayer.CUTOUT)
val tankIOBlock = TankStructureBlock("tank_io", ::TileEntityTankPort)

val salineControllerBlock = MultiBlockRotatableActiveBlock("saline_controller", ::TileEntitySalineController)
val salineWallBlock = PlainMultiBlockPartBlock("saline_wall", ::TileEntityMultiBlockPartSaline, opaque = false)
val salineHeatedWallBlock = PlainMultiBlockPartBlock("saline_heated_wall", ::TileEntityMultiBlockPartSaline, opaque = false)
val salineFluidInputBlock = MultiBlockRotatableActiveBlock("saline_fluid_input", ::TileEntitySalineFluidInput,
        renderLayer = BlockRenderLayer.CUTOUT)
val salineFluidOutputBlock = MultiBlockActiveBlock("saline_fluid_output", ::TileEntitySalineFluidOutput,
        renderLayer = BlockRenderLayer.CUTOUT)
val salineHeatingAgentInputBlock = MultiBlockRotatableActiveBlock("saline_heating_agent_input", ::TileEntitySalineHeatingAgentInput,
        renderLayer = BlockRenderLayer.CUTOUT)
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

val chrysotileBlock = PlainBlock("chrysotile", Material.ROCK).apply { setHardness(1.5f).setResistance(2.3f) }
val asbestosBlock = PlainBlock("asbestos_block", Material.ROCK, oreDictionaryName = "blockAsbestos")
        .apply { setHardness(1.4f).setResistance(5f) }
val saltBlock = PlainBlock("salt_block", Material.ROCK, oreDictionaryName = "blockSalt")
        .apply { setHardness(1.0f).setResistance(2.5f) }
