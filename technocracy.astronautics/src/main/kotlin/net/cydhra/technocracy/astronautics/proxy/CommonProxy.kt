package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.astronautics.TCAstronautics
import net.cydhra.technocracy.astronautics.client.astronauticsCreativeTabs
import net.cydhra.technocracy.astronautics.content.blocks.*
import net.cydhra.technocracy.astronautics.content.capabilities.satellites.DefaultSatelliteOrbit
import net.cydhra.technocracy.astronautics.content.capabilities.satellites.ISatelliteOrbit
import net.cydhra.technocracy.astronautics.content.capabilities.satellites.SatelliteOrbitStorage
import net.cydhra.technocracy.astronautics.content.entity.entityParticleEmitter
import net.cydhra.technocracy.astronautics.content.entity.entityRocket
import net.cydhra.technocracy.astronautics.content.items.concreteCanItem
import net.cydhra.technocracy.astronautics.content.items.concreteSprayerItem
import net.cydhra.technocracy.astronautics.content.tileentity.TileEntityRocketController
import net.cydhra.technocracy.astronautics.dyson.DysonSphereController
import net.cydhra.technocracy.foundation.api.fluids.FluidManager
import net.cydhra.technocracy.foundation.content.blocks.BlockManager
import net.cydhra.technocracy.foundation.content.entities.manager.EntityManager
import net.cydhra.technocracy.foundation.content.items.ItemManager
import net.cydhra.technocracy.foundation.content.tileentities.TileEntityManager
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
        blockManager = BlockManager(TCAstronautics.MODID, astronauticsCreativeTabs)
        fluidManager = FluidManager(blockManager)
        itemManager = ItemManager(TCAstronautics.MODID, astronauticsCreativeTabs)
        tileEntityManager = TileEntityManager(TCAstronautics.MODID)
        entityManager = EntityManager(TCAstronautics.MODID)
    }

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)
        MinecraftForge.EVENT_BUS.register(tileEntityManager)
        MinecraftForge.EVENT_BUS.register(entityManager)
        MinecraftForge.EVENT_BUS.register(CapabilityEventBusHandler())

        blockManager.prepareBlocksForRegistration(scaffoldBlock)
        blockManager.prepareBlocksForRegistration(reinforcedConcreteBlock)
        blockManager.prepareBlocksForRegistration(wetConcreteBlock)
        blockManager.prepareBlocksForRegistration(wetReinforcedConcreteBlock)
        blockManager.prepareBlocksForRegistration(rocketControllerBlock)
        blockManager.prepareBlocksForRegistration(rocketHullBlock)
        blockManager.prepareBlocksForRegistration(rocketDriveBlock)
        blockManager.prepareBlocksForRegistration(rocketTipBlock)
        blockManager.prepareBlocksForRegistration(rocketTank)
        blockManager.prepareBlocksForRegistration(rocketStorageBlock)

        itemManager.prepareItemForRegistration(concreteSprayerItem)
        itemManager.prepareItemForRegistration(concreteCanItem)

        tileEntityManager.prepareTileEntityForRegistration(TileEntityRocketController::class)

        entityManager.prepareEntityForRegistration(entityRocket)
        entityManager.prepareEntityForRegistration(entityParticleEmitter)
    }

    open fun init() {
        CapabilityManager.INSTANCE.register(ISatelliteOrbit::class.java, SatelliteOrbitStorage(),
                ::DefaultSatelliteOrbit)
    }

    open fun postInit() {
        DysonSphereController.initialize()
    }
}