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
import net.cydhra.technocracy.foundation.network.*
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
import net.minecraftforge.fml.relauncher.Side


/**
 * Mod proxy for both client and server implementation. Handles registration of everything required by both sides.
 * Offers a general interface for resource loading though client-only resources aren't loaded by this class
 */
open class CommonProxy {

    private lateinit var materialSystems: Array<OreSystem>

    protected lateinit var blockManager: BlockManager
    protected lateinit var fluidManager: FluidManager
    protected lateinit var itemManager: ItemManager
    protected lateinit var tileEntityManager: TileEntityManager

    /**
     * Initialize the class properties. This should not be done earlier, as contents of the properties might access
     * the config, which isn't loaded at instantiation of the proxy.
     */
    fun initializeProxy() {
        materialSystems = arrayOf(aluminumSystem,
                copperSystem,
                leadSystem,
                lithiumSystem,
                nickelSystem,
                niobiumSystem,
                silverSystem,
                tinSystem,
                ironSystem,
                goldSystem)

        blockManager = BlockManager(TCFoundation.MODID, technocracyCreativeTabs)
        fluidManager = FluidManager(blockManager)
        itemManager = ItemManager(TCFoundation.MODID, technocracyCreativeTabs)
        tileEntityManager = TileEntityManager(TCFoundation.MODID)
    }

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(tileEntityManager)
        MinecraftForge.EVENT_BUS.register(Network)
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)
        MinecraftForge.EVENT_BUS.register(PotionManager)

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
        blockManager.prepareBlocksForRegistration(crystallizationChamberBlock, MachineConnectorModel())
        blockManager.prepareBlocksForRegistration(dissolutionChamberBlock, MachineConnectorModel())
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
        itemManager.prepareItemForRegistration(emptyCan)

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

        tileEntityManager.prepareTileEntityForRegistration(TileEntityAlloySmeltery::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityCentrifuge::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalProcessingChamber::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalEtchingChamber::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalOxidizer::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalReactionChamber::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityCompactor::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityCrystallizationChamber::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityDissolutionChamber::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityElectricFurnace::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityElectrolysisChamber::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityKiln::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityPolymerizationChamber::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityPulverizer::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityPipe::class)

        tileEntityManager.prepareTileEntityForRegistration(TileEntityMultiBlockPartBoiler::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityBoilerController::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityBoilerHeater::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityBoilerInput::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityBoilerOutput::class)

        tileEntityManager.prepareTileEntityForRegistration(TileEntityMultiBlockPartHeatExchanger::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityHeatExchangerController::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityHeatExchangerInput::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityHeatExchangerOutput::class)

        tileEntityManager.prepareTileEntityForRegistration(TileEntityRefineryController::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityRefineryInput::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityRefineryOutput::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityRefineryHeater::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityMultiBlockPartRefinery::class)

        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe::class.java, PipeRenderer())

        PotionManager.preparePotionForRegistration(oilyEffect)

        GameRegistry.registerWorldGenerator(OilLakeGen(), 0)
        GameRegistry.registerWorldGenerator(OilSandGen(), 0)

        NetworkRegistry.INSTANCE.registerGuiHandler(TCFoundation, TCGuiHandler())

        PacketHandler.registerPacket(ItemScrollPacket::class.java, ItemScrollPacket::class.java, Side.SERVER)
        PacketHandler.registerPacket(ItemKeyBindPacket::class.java, ItemKeyBindPacket::class.java, Side.SERVER)
        PacketHandler.registerPacket(MachineInfoRequest::class.java, MachineInfoRequest::class.java, Side.SERVER)
        PacketHandler.registerPacket(MachineInfoResponse::class.java, MachineInfoResponse::class.java, Side.CLIENT)
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