package net.cydhra.technocracy.foundation.proxy

import com.google.common.collect.ImmutableMap
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.client.technocracyCreativeTabs
import net.cydhra.technocracy.foundation.conduits.ConduitNetwork
import net.cydhra.technocracy.foundation.content.blocks.*
import net.cydhra.technocracy.foundation.content.fluids.*
import net.cydhra.technocracy.foundation.content.items.*
import net.cydhra.technocracy.foundation.content.oresystems.*
import net.cydhra.technocracy.foundation.content.potions.oilyEffect
import net.cydhra.technocracy.foundation.content.tileentities.machines.*
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPartBoiler
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPartCapacitor
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPartHeatExchanger
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPartRefinery
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
import net.cydhra.technocracy.foundation.content.tileentities.pipe.TileEntityPipe
import net.cydhra.technocracy.foundation.content.tileentities.storage.TileEntityDrum
import net.cydhra.technocracy.foundation.content.world.OilLakeGen
import net.cydhra.technocracy.foundation.content.world.OilSandGen
import net.cydhra.technocracy.foundation.content.world.WorldGenDeco
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.data.crafting.types.ITIRecipe
import net.cydhra.technocracy.foundation.model.blocks.manager.BlockManager
import net.cydhra.technocracy.foundation.model.fluids.manager.FluidManager
import net.cydhra.technocracy.foundation.model.items.capability.DefaultItemCapability
import net.cydhra.technocracy.foundation.model.items.capability.ICapabilityWrapperCapability
import net.cydhra.technocracy.foundation.model.items.capability.ICapabilityWrapperStorage
import net.cydhra.technocracy.foundation.model.items.manager.ItemManager
import net.cydhra.technocracy.foundation.model.oresystems.api.OreSystem
import net.cydhra.technocracy.foundation.model.potions.manager.PotionManager
import net.cydhra.technocracy.foundation.model.tileentities.manager.TileEntityManager
import net.cydhra.technocracy.foundation.network.ComponentClickPacket
import net.cydhra.technocracy.foundation.network.ItemKeyBindPacket
import net.cydhra.technocracy.foundation.network.ItemScrollPacket
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.cydhra.technocracy.foundation.network.componentsync.*
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.capabilities.CapabilityManager
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
                zirconiumSystem,
                ironSystem,
                goldSystem)

        blockManager = BlockManager(TCFoundation.MODID, technocracyCreativeTabs)
        fluidManager = FluidManager(blockManager)
        itemManager = ItemManager(TCFoundation.MODID, technocracyCreativeTabs)
        tileEntityManager = TileEntityManager(TCFoundation.MODID)
    }

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(tileEntityManager)
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)
        MinecraftForge.EVENT_BUS.register(GuiUpdateListener)
        MinecraftForge.EVENT_BUS.register(PotionManager)
        MinecraftForge.EVENT_BUS.register(ConduitNetwork)

        materialSystems.forEach { it.preInit(it, blockManager, itemManager, fluidManager) }

        fluidManager.registerFluid(mineralOilFluid)
        fluidManager.registerFluid(sulfurDioxideFluid)
        fluidManager.registerFluid(oxygenFluid)
        fluidManager.registerFluid(sulfurTrioxideFluid)
        fluidManager.registerFluid(sulfuricAcidFluid)
        fluidManager.registerFluid(propeneFluid)
        fluidManager.registerFluid(acrylicAcidFluid)
        fluidManager.registerFluid(benzeneFluid)
        fluidManager.registerFluid(keroseneFluid)
        fluidManager.registerFluid(rocketFuelFluid)
        fluidManager.registerFluid(propyleneOxideFluid)
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
        fluidManager.registerFluid(brine)
        fluidManager.registerFluid(lithiumChloride)
        fluidManager.registerFluid(aqueousLithium)
        fluidManager.registerFluid(aqueousSodiumAcrylate)
        fluidManager.registerFluid(glue)
        fluidManager.registerFluid(phenolFluid)
        fluidManager.registerFluid(chloroBenzeneFluid)
        fluidManager.registerFluid(diphenyletherFluid)
        fluidManager.registerFluid(heatTransferOilFluid)
        fluidManager.registerFluid(lyeFluid)

        blockManager.prepareBlocksForRegistration(alloySmelteryBlock)
        blockManager.prepareBlocksForRegistration(centrifugeBlock)
        blockManager.prepareBlocksForRegistration(chemicalProcessingChamberBlock)
        blockManager.prepareBlocksForRegistration(chemicalEtchingChamberBlock)
        blockManager.prepareBlocksForRegistration(chemicalOxidizerBlock)
        blockManager.prepareBlocksForRegistration(chemicalReactionChamberBlock)
        blockManager.prepareBlocksForRegistration(compactorBlock)
        blockManager.prepareBlocksForRegistration(crystallizationChamberBlock)
        blockManager.prepareBlocksForRegistration(dissolutionChamberBlock)
        blockManager.prepareBlocksForRegistration(electricFurnaceBlock)
        blockManager.prepareBlocksForRegistration(electrolysisChamberBlock)
        blockManager.prepareBlocksForRegistration(industrialRefineryBlock)
        blockManager.prepareBlocksForRegistration(kilnBlock)
        blockManager.prepareBlocksForRegistration(polymerizationChamberBlock)
        blockManager.prepareBlocksForRegistration(pulverizerBlock)

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

        blockManager.prepareBlocksForRegistration(capacitorControllerBlock)
        blockManager.prepareBlocksForRegistration(capacitorEnergyPortBlock)
        blockManager.prepareBlocksForRegistration(capacitorConnectorBlock)
        blockManager.prepareBlocksForRegistration(capacitorWallBlock)
        blockManager.prepareBlocksForRegistration(leadBlock)
        blockManager.prepareBlocksForRegistration(leadOxideBlock)

        blockManager.prepareBlocksForRegistration(tankWallBlock)
        blockManager.prepareBlocksForRegistration(tankIOBlock)
        blockManager.prepareBlocksForRegistration(tankGlassBlock)

        blockManager.prepareBlocksForRegistration(salineWallBlock)
        blockManager.prepareBlocksForRegistration(salineHeatedWallBlock)
        blockManager.prepareBlocksForRegistration(salineControllerBlock)
        blockManager.prepareBlocksForRegistration(salineFluidInput)
        blockManager.prepareBlocksForRegistration(salineFluidOutput)
        blockManager.prepareBlocksForRegistration(salineHeatingAgentInput)
        blockManager.prepareBlocksForRegistration(salineHeatingAgentOutput)

        blockManager.prepareBlocksForRegistration(oilSandBlock)
        blockManager.prepareBlocksForRegistration(oilStone)
        blockManager.prepareBlocksForRegistration(oilBlock)
        blockManager.prepareBlocksForRegistration(drum)
        blockManager.prepareBlocksForRegistration(leadGlassPaneBlock)

        blockManager.prepareBlocksForRegistration(sulfuricAcidBlock)

        blockManager.prepareBlocksForRegistration(pipe)
        blockManager.prepareBlocksForRegistration(chrysotileBlock)
        blockManager.prepareBlocksForRegistration(asbestosBlock)
        blockManager.prepareBlocksForRegistration(saltBlock)

        itemManager.prepareItemForRegistration(machineFrameItem)
        itemManager.prepareItemForRegistration(advancedMachineFrameItem)
        itemManager.prepareItemForRegistration(industrialMachineFrameItem)
        itemManager.prepareItemForRegistration(coalDustItem)
        itemManager.prepareItemForRegistration(sulfurItem)
        itemManager.prepareItemForRegistration(batteryItem)
        itemManager.prepareItemForRegistration(siliconItem)
        itemManager.prepareItemForRegistration(siliconChlorideItem)
        itemManager.prepareItemForRegistration(saltItem)
        itemManager.prepareItemForRegistration(sodiumAcrylateItem)
        itemManager.prepareItemForRegistration(rubberItem)
        itemManager.prepareItemForRegistration(polyacrylateItem)
        itemManager.prepareItemForRegistration(polypropyleneItem)
        itemManager.prepareItemForRegistration(polystyreneItem)
        itemManager.prepareItemForRegistration(nanotubesItem)
        itemManager.prepareItemForRegistration(mirrorItem)
        itemManager.prepareItemForRegistration(polishedMirrorItem)
        itemManager.prepareItemForRegistration(emptyCanItem)
        itemManager.prepareItemForRegistration(wrenchItem)
        itemManager.prepareItemForRegistration(circuitBoardItem)
        itemManager.prepareItemForRegistration(glueBallItem)
        itemManager.prepareItemForRegistration(asbestosItem)
        itemManager.prepareItemForRegistration(biphenylItem)

        itemManager.prepareItemForRegistration(membraneItem)
        itemManager.prepareItemForRegistration(ironRodItem)
        itemManager.prepareItemForRegistration(coilItem)
        itemManager.prepareItemForRegistration(servoItem)
        itemManager.prepareItemForRegistration(polyfibreItem)
        itemManager.prepareItemForRegistration(pumpItem)
        itemManager.prepareItemForRegistration(fanItem)
        itemManager.prepareItemForRegistration(spandexItem)
        itemManager.prepareItemForRegistration(cfrpItem)

        itemManager.prepareItemForRegistration(invarItem)
        itemManager.prepareItemForRegistration(siliconBronzeItem)
        itemManager.prepareItemForRegistration(superconductorItem)
        itemManager.prepareItemForRegistration(metallicPhaseChangeMaterialItem)
        itemManager.prepareItemForRegistration(steelItem)
        itemManager.prepareItemForRegistration(bronzeItem)
        itemManager.prepareItemForRegistration(lightAlloyItem)
        itemManager.prepareItemForRegistration(toughAlloyItem)
        itemManager.prepareItemForRegistration(electrumItem)

        itemManager.prepareItemForRegistration(invarSheetItem)
        itemManager.prepareItemForRegistration(steelSheetItem)
        itemManager.prepareItemForRegistration(bronzeSheetItem)
        itemManager.prepareItemForRegistration(invarGearItem)
        itemManager.prepareItemForRegistration(steelGearItem)
        itemManager.prepareItemForRegistration(steelGritItem)

        itemManager.prepareItemForRegistration(pipeItem)
        itemManager.prepareItemForRegistration(facadeItem)

        itemManager.prepareItemForRegistration(upgradeSlotItem)
        itemManager.prepareItemForRegistration(upgradeFrameItem)
        itemManager.prepareItemForRegistration(machineSpeedUp1Item)
        itemManager.prepareItemForRegistration(machineSpeedUp2Item)
        itemManager.prepareItemForRegistration(machineSpeedUp3Item)
        itemManager.prepareItemForRegistration(machineSpeedUp4Item)
        itemManager.prepareItemForRegistration(machineEnergyUp1Item)
        itemManager.prepareItemForRegistration(machineEnergyUp2Item)
        itemManager.prepareItemForRegistration(machineEnergyUp3Item)
        itemManager.prepareItemForRegistration(machineEnergyUp4Item)
        itemManager.prepareItemForRegistration(machineAdditiveEnergy1Item)
        itemManager.prepareItemForRegistration(machineAdditiveEnergy2Item)
        itemManager.prepareItemForRegistration(machineAdditiveSpeed1Item)
        itemManager.prepareItemForRegistration(machineAdditiveSpeed2Item)

        itemManager.prepareItemForRegistration(machineLubricantUpItem)
        itemManager.prepareItemForRegistration(machineCoolerUpgradeItem)

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
        tileEntityManager.prepareTileEntityForRegistration(TileEntityIndustrialRefinery::class)
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

        tileEntityManager.prepareTileEntityForRegistration(TileEntityCapacitorController::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityCapacitorEnergyPort::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityMultiBlockPartCapacitor::class)

        tileEntityManager.prepareTileEntityForRegistration(TileEntityTankMultiBlockPart::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityTankPort::class)

        tileEntityManager.prepareTileEntityForRegistration(TileEntityMultiBlockPartSaline::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntitySalineController::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntitySalineFluidInput::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntitySalineFluidOutput::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntitySalineHeatingAgentInput::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntitySalineHeatingAgentOutput::class)

        tileEntityManager.prepareTileEntityForRegistration(TileEntityDrum::class)
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe::class.java, PipeRenderer())

        PotionManager.preparePotionForRegistration(oilyEffect)

        GameRegistry.registerWorldGenerator(OilLakeGen(), 0)
        GameRegistry.registerWorldGenerator(OilSandGen(), 0)
        GameRegistry.registerWorldGenerator(WorldGenDeco(), 0)

        NetworkRegistry.INSTANCE.registerGuiHandler(TCFoundation, TCGuiHandler())

        PacketHandler.registerPacket(ItemScrollPacket::class.java, ItemScrollPacket::class.java, Side.SERVER)
        PacketHandler.registerPacket(ItemKeyBindPacket::class.java, ItemKeyBindPacket::class.java, Side.SERVER)
        PacketHandler.registerPacket(MachineInfoPacket::class.java, MachineInfoPacket::class.java, Side.CLIENT)
        PacketHandler.registerPacket(ClientRequestSyncPacket::class.java, ClientRequestSyncPacket::class.java, Side.SERVER)
        PacketHandler.registerPacket(ComponentClickPacket::class.java, ComponentClickPacket::class.java, Side.SERVER)
        PacketHandler.registerPacket(ClientSwitchTabPacket::class.java, ClientSwitchTabPacket::class.java, Side.SERVER)
        PacketHandler.registerPacket(ClientChangeSideConfigPacket::class.java, ClientChangeSideConfigPacket::class.java, Side.SERVER)
    }

    open fun init() {
        materialSystems.forEach { it.init(it) }
        CapabilityManager.INSTANCE.register(ICapabilityWrapperCapability::class.java, ICapabilityWrapperStorage(), ::DefaultItemCapability)
    }

    open fun postInit() {
        // register all furnace recipes within electrical furnace
        FurnaceRecipes.instance().smeltingList.forEach { recipe ->
            RecipeManager.registerRecipe(
                    type = RecipeManager.RecipeType.ELECTRIC_FURNACE,
                    recipe = ITIRecipe(
                            inputItem = Ingredient.fromStacks(recipe.key),
                            outputItem = recipe.value,
                            // the processing cost is derived from EXP output, as more valuable items give more exp
                            // and thus should be expected to be more late game.
                            processingCost = 60 +
                                    (100 * FurnaceRecipes.instance().getSmeltingExperience(recipe.value)).toInt()
                    ))
        }

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
