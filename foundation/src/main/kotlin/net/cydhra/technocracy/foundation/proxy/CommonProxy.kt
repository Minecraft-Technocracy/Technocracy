package net.cydhra.technocracy.foundation.proxy

import com.google.common.collect.ImmutableMap
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.client.model.MachineConnectorModel
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.items.general.*
import net.cydhra.technocracy.foundation.liquids.general.FluidManager
import net.cydhra.technocracy.foundation.liquids.general.oilFluid
import net.cydhra.technocracy.foundation.materialsystems.*
import net.cydhra.technocracy.foundation.potions.PotionManager
import net.cydhra.technocracy.foundation.potions.oilyEffect
import net.cydhra.technocracy.foundation.tileentity.*
import net.cydhra.technocracy.foundation.tileentity.management.TileEntityManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.model.animation.IAnimationStateMachine

/**
 * Mod proxy for both client and server implementation. Handles registration of everything required by both sides.
 * Offers a general interface for resource loading though client-only resources aren't loaded by this class
 */
open class CommonProxy {

    private val materialSystems = arrayOf(aluminiumSystem, copperSystem, leadSystem,
            lithiumSystem, nickelSystem, osmiumSystem, silverSystem, tinSystem)

    open fun preInit() {
        materialSystems.forEach(MaterialSystem::preInit)

        FluidManager.registerFluid(oilFluid)

        BlockManager.prepareBlocksForRegistration(alloySmelteryBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(arcFurnaceBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(centrifugeBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(chemicalEnrichmentChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(chemicalEtchingChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(chemicalOxidizerBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(chemicalReactionChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(compactorBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(electricFurnaceBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(electrolysisChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(kilnBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(polymerizationChamberBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(pulverizerBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(refineryBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(thermoelectricFreezerBlock, MachineConnectorModel())

        BlockManager.prepareBlocksForRegistration(oilSandBlock)
        BlockManager.prepareBlocksForRegistration(ironBeamBlock)
        BlockManager.prepareBlocksForRegistration(oilBlock)

        ItemManager.prepareItemForRegistration(machineFrameItem)
        ItemManager.prepareItemForRegistration(coalDustItem)
        ItemManager.prepareItemForRegistration(ironDustItem)
        ItemManager.prepareItemForRegistration(ironSheetItem)
        ItemManager.prepareItemForRegistration(batteryItem)
        ItemManager.prepareItemForRegistration(akkumulatorItem)
        ItemManager.prepareItemForRegistration(siliconItem)
        ItemManager.prepareItemForRegistration(bedrockiumItem)

        TileEntityManager.prepareTileEntityForRegistration(TileEntityAlloySmeltery::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityArcFurnace::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityCentrifuge::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalEnrichmentChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalEtchingChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalOxidizer::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityChemicalReactionChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityCompactor::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityElectricFurnace::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityElectrolysisChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityKiln::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityPolymerizationChamber::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityPulverizer::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityRefinery::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityThermoelectricFreezer::class)

        PotionManager.preparePotionForRegistration(oilyEffect)
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