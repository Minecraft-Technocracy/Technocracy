package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.*
import net.cydhra.technocracy.foundation.blocks.liquid.OilBlock
import net.cydhra.technocracy.foundation.tileentity.machines.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPartBoiler
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPartHeatExchanger
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerHeater
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerOutput
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerOutput
import net.minecraft.block.material.Material
import net.minecraft.util.BlockRenderLayer

val alloySmelteryBlock = MachineBlock("alloy_smeltery", ::TileEntityAlloySmeltery)
val centrifugeBlock = MachineBlock("centrifuge", ::TileEntityCentrifuge)
val chemicalProcessingChamberBlock = MachineBlock("chemical_processing_chamber", ::TileEntityChemicalProcessingChamber)
val chemicalEtchingChamberBlock = MachineBlock("chemical_etching_chamber", ::TileEntityChemicalEtchingChamber)
val chemicalOxidizerBlock = MachineBlock("chemical_oxidizer", ::TileEntityChemicalOxidizer)
val chemicalReactionChamberBlock = MachineBlock("chemical_reaction_chamber", ::TileEntityChemicalReactionChamber)
val compactorBlock = MachineBlock("compactor", ::TileEntityCompactor)
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

val oilSandBlock = OilSand()
val oilStone = OilStone()
val ironBeamBlock = IronBeamBlock()

val oilBlock = OilBlock()

val pipe = PipeBlock()