package net.cydhra.technocracy.optics.proxy

import net.cydhra.technocracy.foundation.model.blocks.manager.BlockManager
import net.cydhra.technocracy.foundation.model.entities.manager.EntityManager
import net.cydhra.technocracy.foundation.model.fluids.manager.FluidManager
import net.cydhra.technocracy.foundation.model.items.manager.ItemManager
import net.cydhra.technocracy.foundation.model.tileentities.manager.TileEntityManager
import net.cydhra.technocracy.optics.TCOptics
import net.cydhra.technocracy.optics.api.capabilities.laser.*
import net.cydhra.technocracy.optics.client.opticsCreativeTab
import net.cydhra.technocracy.optics.content.blocks.laserDrillBlock
import net.cydhra.technocracy.optics.content.blocks.laserEmitterBlock
import net.cydhra.technocracy.optics.content.tileentities.machines.TileEntityLaser
import net.cydhra.technocracy.optics.content.tileentities.machines.TileEntityLaserDrill
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager

open class CommonProxy {

    protected lateinit var blockManager: BlockManager
    protected lateinit var fluidManager: FluidManager
    protected lateinit var itemManager: ItemManager
    protected lateinit var tileEntityManager: TileEntityManager
    protected lateinit var entityManager: EntityManager

    /**
     * Initialize the class properties. This should not be done earlier, as contents of the properties might access
     * the config, which isn't loaded at instantiation of the proxy.
     */
    fun initializeProxy() {
        blockManager = BlockManager(TCOptics.MODID, opticsCreativeTab)
        fluidManager = FluidManager(blockManager)
        itemManager = ItemManager(TCOptics.MODID, opticsCreativeTab)
        tileEntityManager = TileEntityManager(TCOptics.MODID)
        entityManager = EntityManager(TCOptics.MODID)
    }

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)
        MinecraftForge.EVENT_BUS.register(tileEntityManager)
        MinecraftForge.EVENT_BUS.register(entityManager)

        blockManager.prepareBlocksForRegistration(laserEmitterBlock)
        blockManager.prepareBlocksForRegistration(laserDrillBlock)

        tileEntityManager.prepareTileEntityForRegistration(TileEntityLaser::class)
        tileEntityManager.prepareTileEntityForRegistration(TileEntityLaserDrill::class)
    }

    open fun init() {
        CapabilityManager.INSTANCE.register(ILaserEmitter::class.java, LaserEmitterStorage(), ::DefaultLaserEmitter)
        CapabilityManager.INSTANCE.register(ILaserAbsorber::class.java, LaserAbsorberStorage(), ::DefaultLaserAbsorber)
    }

    open fun postInit() {

    }
}