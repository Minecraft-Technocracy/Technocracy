package net.cydhra.technocracy.foundation.proxy

import com.google.common.collect.ImmutableMap
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.client.model.MachineConnectorModel
import net.cydhra.technocracy.foundation.items.general.*
import net.cydhra.technocracy.foundation.materials.*
import net.cydhra.technocracy.foundation.tileentity.TileEntityElectricFurnace
import net.cydhra.technocracy.foundation.tileentity.TileEntityPulverizer
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

        BlockManager.prepareBlocksForRegistration(pulverizerBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(electricFurnaceBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(centrifugeBlock, MachineConnectorModel())
        BlockManager.prepareBlocksForRegistration(oilSandBlock)
        BlockManager.prepareBlocksForRegistration(ironBeamBlock)

        ItemManager.prepareItemForRegistration(machineFrameItem)
        ItemManager.prepareItemForRegistration(coalDustItem)
        ItemManager.prepareItemForRegistration(ironDustItem)
        ItemManager.prepareItemForRegistration(ironSheetItem)
        ItemManager.prepareItemForRegistration(batteryItem)
        ItemManager.prepareItemForRegistration(akkumulatorItem)

        TileEntityManager.prepareTileEntityForRegistration(TileEntityPulverizer::class)
        TileEntityManager.prepareTileEntityForRegistration(TileEntityElectricFurnace::class)
    }

    open fun init() {
        materialSystems.forEach(MaterialSystem::init)
    }

    open fun postInit() {

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