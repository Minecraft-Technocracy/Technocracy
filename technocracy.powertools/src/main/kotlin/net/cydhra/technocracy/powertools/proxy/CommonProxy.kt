package net.cydhra.technocracy.powertools.proxy

import net.cydhra.technocracy.foundation.model.blocks.manager.BlockManager
import net.cydhra.technocracy.foundation.model.entities.manager.EntityManager
import net.cydhra.technocracy.foundation.model.fluids.manager.FluidManager
import net.cydhra.technocracy.foundation.model.items.manager.ItemManager
import net.cydhra.technocracy.foundation.model.tileentities.manager.TileEntityManager
import net.cydhra.technocracy.powertools.TCPowertools
import net.cydhra.technocracy.powertools.client.powertoolsCreativeTab
import net.cydhra.technocracy.powertools.content.item.batteryUpgrade_One
import net.cydhra.technocracy.powertools.content.item.energyShield
import net.cydhra.technocracy.powertools.content.item.modularItem
import net.minecraftforge.common.MinecraftForge

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
        blockManager = BlockManager(TCPowertools.MODID, powertoolsCreativeTab)
        fluidManager = FluidManager(blockManager)
        itemManager = ItemManager(TCPowertools.MODID, powertoolsCreativeTab)
        tileEntityManager = TileEntityManager(TCPowertools.MODID)
        entityManager = EntityManager(TCPowertools.MODID)
    }

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)
        MinecraftForge.EVENT_BUS.register(tileEntityManager)
        MinecraftForge.EVENT_BUS.register(entityManager)

        itemManager.prepareItemForRegistration(batteryUpgrade_One)
        itemManager.prepareItemForRegistration(modularItem)
        itemManager.prepareItemForRegistration(energyShield)
    }

    open fun init() {

    }

    open fun postInit() {

    }
}