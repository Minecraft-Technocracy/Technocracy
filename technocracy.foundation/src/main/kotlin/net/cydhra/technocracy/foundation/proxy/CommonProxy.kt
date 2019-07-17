package net.cydhra.technocracy.foundation.proxy

import com.google.common.collect.ImmutableMap
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.client.model.customModel.connector.MachineConnectorModel
import net.cydhra.technocracy.foundation.client.model.facade.FacadeItemModel
import net.cydhra.technocracy.foundation.client.model.pipe.PipeItemModel
import net.cydhra.technocracy.foundation.client.model.pipe.PipeModel
import net.cydhra.technocracy.foundation.client.technocracyCreativeTabs
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.items.general.*
import net.cydhra.technocracy.foundation.liquids.general.*
import net.cydhra.technocracy.foundation.oresystems.*
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
import net.cydhra.technocracy.foundation.world.gen.OilLakeGen
import net.cydhra.technocracy.foundation.world.gen.OilSandGen
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.model.animation.IAnimationStateMachine
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry


/**
 * Mod proxy for both client and server implementation. Handles registration of everything required by both sides.
 * Offers a general interface for resource loading though client-only resources aren't loaded by this class
 */
open class CommonProxy {

    private val materialSystems = arrayOf(aluminumSystem,
            copperSystem,
            leadSystem,
            lithiumSystem,
            nickelSystem,
            niobiumSystem,
            silverSystem,
            tinSystem,
            ironSystem,
            goldSystem)

    protected val blockManager = BlockManager(technocracyCreativeTabs)
    protected val fluidManager = FluidManager(blockManager)
    protected val itemManager = ItemManager(technocracyCreativeTabs)

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(Network)
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)

        materialSystems.forEach { it.preInit(it, blockManager, itemManager, fluidManager) }

        fluidManager.registerFluid(mineralOilFluid)
        fluidManager.registerFluid(sulfurDioxideFluid)
        fluidManager.registerFluid(oxygenFluid)
        fluidManager.registerFluid(sulfurTrioxideFluid)
        fluidManager.registerFluid(sulfuricAcidFluid)
        fluidManager.registerFluid(propeneFluid)
        fluidManager.registerFluid(acrylicAcidFluid)
        fluidManager.registerFluid(benzeneFluid)
        fluidManager.registerFluid(phenolFluid)
        fluidManager.registerFluid(keroseneFluid)
        fluidManager.registerFluid(rocketFuelFluid)
        fluidManager.registerFluid(propyleneOxideFluid)
        fluidManager.registerFluid(propyleneGlycolFluid)
        fluidManager.registerFluid(chlorineFluid)
        fluidManager.registerFluid(styreneFluid)
        fluidManager.registerFluid(cryogenicGelFluid)
        fluidManager.registerFluid(heavyOilFluid)
        fluidManager.registerFluid(lightOilFluid)
        fluidManager.registerFluid(tarFluid)
        fluidManager.registerFluid(pitchFluid)
        fluidManager.registerFluid(hydrochloricAcidFluid)
        fluidManager.registerFluid(hydrogenFluid)
        fluidManager.registerFluid(silicaFluid)
        fluidManager.registerFluid(steamFluid)
        fluidManager.registerFluid(drossFluid)

        blockManager.prepareBlocksForRegistration(alloySmelteryBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(centrifugeBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(chemicalProcessingChamberBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(chemicalEtchingChamberBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(chemicalOxidizerBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(chemicalReactionChamberBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(compactorBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(electricFurnaceBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(electrolysisChamberBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(kilnBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(polymerizationChamberBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(pulverizerBlock, MachineConnectorModel())

        blockManager.prepareBlocksForRegistration(boilerControllerBlock)
        blockManager.prepareBlocksForRegistration(boilerHeaterBlock)
        blockManager.prepareBlocksForRegistration(boilerFluidInputBlock)
        blockManager.prepareBlocksForRegistration(boilerFluidOutputBlock)
        blockManager.prepareBlocksForRegistration(boilerWallBlock)
        blockManager.prepareBlocksForRegistration(boilerGlassBlock)
        blockManager.prepareBlocksForRegistration(boilerConductorBlock)

        blockManager.prepareBlocksForRegistration(heatExchangerControllerBlock)
        blockManager.prepareBlocksForRegistration(heatExchangerWallBlock)
        blockManager.prepareBlocksForRegistration(heatExchangerGlassBlock)
        blockManager.prepareBlocksForRegistration(heatExchangerColdAgentTube)
        blockManager.prepareBlocksForRegistration(heatExchangerHotAgentTube)
        blockManager.prepareBlocksForRegistration(heatExchangerInputBlock)
        blockManager.prepareBlocksForRegistration(heatExchangerOutputBlock)

        blockManager.prepareBlocksForRegistration(refineryControllerBlock)
        blockManager.prepareBlocksForRegistration(refineryWallBlock)
        blockManager.prepareBlocksForRegistration(refineryInputBlock)
        blockManager.prepareBlocksForRegistration(refineryOutputBlock)
        blockManager.prepareBlocksForRegistration(refineryHeaterBlock)

        blockManager.prepareBlocksForRegistration(oilSandBlock)
        blockManager.prepareBlocksForRegistration(oilStone)
        blockManager.prepareBlocksForRegistration(ironBeamBlock)
        blockManager.prepareBlocksForRegistration(oilBlock)

        blockManager.prepareBlocksForRegistration(pipe, PipeModel())

        itemManager.prepareItemForRegistration(machineFrameItem)
        itemManager.prepareItemForRegistration(coalDustItem)
        itemManager.prepareItemForRegistration(sulfurItem)
        itemManager.prepareItemForRegistration(batteryItem)
        itemManager.prepareItemForRegistration(siliconItem)
        itemManager.prepareItemForRegistration(bedrockiumItem)
        itemManager.prepareItemForRegistration(siliconChlorideItem)
        itemManager.prepareItemForRegistration(saltItem)
        itemManager.prepareItemForRegistration(sodiumAcrylateItem)
        itemManager.prepareItemForRegistration(rubberItem)
        itemManager.prepareItemForRegistration(calciumAcetateItem)
        itemManager.prepareItemForRegistration(phenoplastItem)
        itemManager.prepareItemForRegistration(polyacrylateItem)
        itemManager.prepareItemForRegistration(polypropyleneItem)
        itemManager.prepareItemForRegistration(polystyreneItem)
        itemManager.prepareItemForRegistration(nanotubesItem)
        itemManager.prepareItemForRegistration(mirrorItem)
        itemManager.prepareItemForRegistration(polishedMirrorItem)

        itemManager.prepareItemForRegistration(invarItem)
        itemManager.prepareItemForRegistration(siliconBronzeItem)
        itemManager.prepareItemForRegistration(superconductorItem)
        itemManager.prepareItemForRegistration(metallicPhaseChangeMaterialItem)
        itemManager.prepareItemForRegistration(steelItem)
        itemManager.prepareItemForRegistration(bronzeItem)
        itemManager.prepareItemForRegistration(lightAlloyItem)
        itemManager.prepareItemForRegistration(toughAlloyItem)

        itemManager.prepareItemForRegistration(pipeItem, PipeItemModel())
        itemManager.prepareItemForRegistration(facadeItem, FacadeItemModel())

        if (!Minecraft.getMinecraft().isSingleplayer) {
            //Dev tools
            itemManager.prepareItemForRegistration(structureMarker)
            MinecraftForge.EVENT_BUS.register(structureMarker)
        }

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

        GameRegistry.registerWorldGenerator(OilLakeGen(), 0)
        GameRegistry.registerWorldGenerator(OilSandGen(), 0)

        NetworkRegistry.INSTANCE.registerGuiHandler(TCFoundation, TCGuiHandler())
    }

    open fun init() {
        materialSystems.forEach { it.init(it) }
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
    open fun loadAnimationStateMachine(location: ResourceLocation,
            parameters: ImmutableMap<String, ITimeValue>): IAnimationStateMachine? {
        return null
    }
}