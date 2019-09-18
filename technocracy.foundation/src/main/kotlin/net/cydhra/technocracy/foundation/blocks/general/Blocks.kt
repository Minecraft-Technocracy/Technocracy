package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.*
import net.cydhra.technocracy.foundation.blocks.liquid.OilBlock
import net.cydhra.technocracy.foundation.blocks.liquid.SulfuricAcidBlock
import net.cydhra.technocracy.foundation.tileentity.machines.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerHeater
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerOutput
import net.cydhra.technocracy.foundation.tileentity.multiblock.capacitor.TileEntityCapacitorController
import net.cydhra.technocracy.foundation.tileentity.multiblock.capacitor.TileEntityCapacitorEnergyPort
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerOutput
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryController
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryHeater
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryOutput
import net.cydhra.technocracy.foundation.tileentity.multiblock.tank.TileEntityTankMultiBlockPart
import net.cydhra.technocracy.foundation.tileentity.multiblock.tank.TileEntityTankPort
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
val electrolysisChamberBlock = MachineBlock("electrolysis_chamber", ::TileEntityElectrolysisChamber)
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

val capacitorControllerBlock = MultiBlockRotatableActiveBlock("capacitor_controller",
        ::TileEntityCapacitorController)
val capacitorWallBlock = PlainMultiBlockPartBlock("capacitor_wall", ::TileEntityMultiBlockPartCapacitor)
val capacitorConnectorBlock = PlainMultiBlockPartBlock("capacitor_connector", ::TileEntityMultiBlockPartCapacitor)
val capacitorEnergyPortBlock = MultiBlockRotatableActiveBlock("capacitor_energy_port", ::TileEntityCapacitorEnergyPort)

val tankWallBlock = PlainMultiBlockPartBlock("tank_wall", ::TileEntityTankMultiBlockPart)
val tankGlassBlock = PlainMultiBlockPartBlock("tank_glass", ::TileEntityTankMultiBlockPart, isFullCube = false, opaque = false, glassSides = true, renderLayer = BlockRenderLayer.CUTOUT)
val tankIOBlock = PlainMultiBlockPartBlock("tank_io", ::TileEntityTankPort)

val leadBlock = PlainBlock("lead_block", Material.IRON)
val leadOxideBlock = PlainBlock("lead_oxide_block", Material.IRON)

val oilSandBlock = OilSandBlock()
val oilStone = OilStoneBlock()

val oilBlock = OilBlock()
val sulfuricAcidBlock = SulfuricAcidBlock()

val pipe = PipeBlock()
val drum = DrumBlock()
val leadGlassPaneBlock = PlainBlock("lead_glass_pane", Material.GLASS, opaque = false, renderLayer = BlockRenderLayer.TRANSLUCENT)
        .apply { setHardness(1.5f).setResistance(1f) }
