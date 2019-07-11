package net.cydhra.technocracy.foundation.proxy

import com.google.common.collect.ImmutableMap
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.client.model.customModel.connector.MachineConnectorModel
import net.cydhra.technocracy.foundation.client.model.facade.FacadeItemModel
import net.cydhra.technocracy.foundation.client.model.pipe.PipeItemModel
import net.cydhra.technocracy.foundation.client.model.pipe.PipeModel
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.items.general.*
import net.cydhra.technocracy.foundation.liquids.general.*
import net.cydhra.technocracy.foundation.materialsystems.*
import net.cydhra.technocracy.foundation.pipes.Network
import net.cydhra.technocracy.foundation.potions.PotionManager
import net.cydhra.technocracy.foundation.potions.oilyEffect
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.cydhra.technocracy.foundation.tileentity.machines.*
import net.cydhra.technocracy.foundation.tileentity.management.TileEntityManager
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPartBoiler
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPartHeatExchanger
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPartRefinery
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerHeater
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerOutput
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerOutput
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryController
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryHeater
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryOutput
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.model.animation.IAnimationStateMachine
import net.minecraftforge.fml.common.network.NetworkRegistry


/**
 * Mod proxy for both client and server implementation. Handles registration of everything required by both sides.
 * Offers a general interface for resource loading though client-only resources aren't loaded by this class
 */
open class CommonProxy {

    private val materialSystems = arrayOf(aluminiumSystem, copperSystem, leadSystem,
            lithiumSystem, nickelSystem, niobiumSystem, silverSystem, tinSystem)

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(Network)

        materialSystems.forEach(MaterialSystem::preInit)

        FluidManager.registerFluid(mineralOilFluid)
        FluidManager.registerFluid(sulfurDioxideFluid)
        FluidManager.registerFluid(oxygenFluid)
        FluidManager.registerFluid(sulfurTrioxideFluid)
        FluidManager.registerFluid(sulfuricAcidFluid)
        FluidManager.registerFluid(propeneFluid)
        FluidManager.registerFluid(acrylicAcidFluid)
        FluidManager.registerFluid(benzeneFluid)
        FluidManager.registerFluid(phenolFluid)
        FluidManager.registerFluid(keroseneFluid)
        FluidManager.registerFluid(rocketFuelFluid)
        FluidManager.registerFluid(propyleneOxideFluid)
        FluidManager.registerFluid(propyleneGlycolFluid)
        FluidManager.registerFluid(chlorineFluid)
        FluidManager.registerFluid(styreneFluid)
        FluidManager.registerFluid(cryogenicGelFluid)
        FluidManager.registerFluid(heavyOilFluid)
        FluidManager.registerFluid(lightOilFluid)
        FluidManager.registerFluid(tarFluid)
        FluidManager.registerFluid(pitchFluid)
        FluidManager.registerFluid(hydrochloricAcidFluid)
        FluidManager.registerFluid(hydrogenFluid)
        FluidManager.registerFluid(silicaFluid)
        FluidManager.registerFluid(steamFluid)

        BlockManager.prepareBlocksForRegistration(alloySmelteryBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(centrifugeBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(chemicalProcessingChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(chemicalEtchingChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(chemicalOxidizerBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(chemicalReactionChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(compactorBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(electricFurnaceBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(electrolysisChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(kilnBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(polymerizationChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(pulverizerBlock, MachineConnectorModel())

        BlockManager.prepareBlocksForRegistration(boilerControllerBlock)
        BlockManager.prepareBlocksForRegistration(boilerHeaterBlock)
        BlockManager.prepareBlocksForRegistration(boilerFluidInputBlock)
        BlockManager.prepareBlocksForRegistration(boilerFluidOutputBlock)
        BlockManager.prepareBlocksForRegistration(boilerWallBlock)
        BlockManager.prepareBlocksForRegistration(boilerGlassBlock)
        BlockManager.prepareBlocksForRegistration(boilerConductorBlock)

        BlockManager.prepareBlocksForRegistration(heatExchangerControllerBlock)
        BlockManager.prepareBlocksForRegistration(heatExchangerWallBlock)
        BlockManager.prepareBlocksForRegistration(heatExchangerGlassBlock)
        BlockManager.prepareBlocksForRegistration(heatExchangerColdAgentTube)
        BlockManager.prepareBlocksForRegistration(heatExchangerHotAgentTube)
        BlockManager.prepareBlocksForRegistration(heatExchangerInputBlock)
        BlockManager.prepareBlocksForRegistration(heatExchangerOutputBlock)

        BlockManager.prepareBlocksForRegistration(refineryControllerBlock)
        BlockManager.prepareBlocksForRegistration(refineryWallBlock)
        BlockManager.prepareBlocksForRegistration(refineryInputBlock)
        BlockManager.prepareBlocksForRegistration(refineryOutputBlock)
        BlockManager.prepareBlocksForRegistration(refineryHeaterBlock)

        BlockManager.prepareBlocksForRegistration(oilSandBlock)
        BlockManager.prepareBlocksForRegistration(oilStone)
        BlockManager.prepareBlocksForRegistration(ironBeamBlock)
        BlockManager.prepareBlocksForRegistration(oilBlock)

        BlockManager.prepareBlocksForRegistration(pipe, PipeModel())

        ItemManager.prepareItemForRegistration(machineFrameItem)
        ItemManager.prepareItemForRegistration(coalDustItem)
        ItemManager.prepareItemForRegistration(ironDustItem)
        ItemManager.prepareItemForRegistration(ironSheetItem)
        ItemManager.prepareItemForRegistration(sulfur)
        ItemManager.prepareItemForRegistration(batteryItem)
        ItemManager.prepareItemForRegistration(siliconItem)
        ItemManager.prepareItemForRegistration(bedrockiumItem)
        ItemManager.prepareItemForRegistration(phenoplastItem)
        ItemManager.prepareItemForRegistration(polyacrylateItem)
        ItemManager.prepareItemForRegistration(polypropyleneItem)
        ItemManager.prepareItemForRegistration(polystyreneItem)
        ItemManager.prepareItemForRegistration(nanotubes)
        ItemManager.prepareItemForRegistration(invarItem)
        ItemManager.prepareItemForRegistration(siliconBronzeItem)
        ItemManager.prepareItemForRegistration(superconductorItem)
        ItemManager.prepareItemForRegistration(metallicPhaseChangeMaterialItem)
        ItemManager.prepareItemForRegistration(pipeItem, PipeItemModel())
        ItemManager.prepareItemForRegistration(facadeItem, FacadeItemModel())

        TileEntityManager.prepareTileEntityForRegistration(TileEntityAlloySmeltery::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityCentrifuge::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalProcessingChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalEtchingChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalOxidizer::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalReactionChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityCompactor::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityElectricFurnace::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityElectrolysisChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityKiln::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityPolymerizationChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityPulverizer::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityPipe::class)

        TileEntityManager.prepareTileEntityForRegistration(TileEntityMultiBlockPartBoiler::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityBoilerController::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityBoilerHeater::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityBoilerInput::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityBoilerOutput::class)

        TileEntityManager.prepareTileEntityForRegistration(TileEntityMultiBlockPartHeatExchanger::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityHeatExchangerController::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityHeatExchangerInput::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityHeatExchangerOutput::class)

        TileEntityManager.prepareTileEntityForRegistration(TileEntityRefineryController::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityRefineryInput::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityRefineryOutput::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityRefineryHeater::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityMultiBlockPartRefinery::class)

        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe::class.java, PipeRenderer())

        PotionManager.preparePotionForRegistration(oilyEffect)

        NetworkRegistry.INSTANCE.registerGuiHandler(TCFoundation, TCGuiHandler())
    }

    open fun init() {
        materialSystems.forEach(MaterialSystem::init)
    }

    open fun postInit() {
        RecipeManager.initialize()
    }

    /**
     * Loads an [IAnimationStateMachine] from a model file at [location] on client side, returns null on server side.
     *
     * @param location ASM model file location
     * @param parameters ASM parameter list
     */
    open fun loadAnimationStateMachine(location: ResourceLocation, parameters: ImmutableMap<String, ITimeValue>):
            IAnimationStateMachine? {
        return null
    }
}