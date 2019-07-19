package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.astronautics.TCAstronautics
import net.cydhra.technocracy.astronautics.blocks.general.reinforcedConcreteBlock
import net.cydhra.technocracy.astronautics.blocks.general.scaffoldBlock
import net.cydhra.technocracy.astronautics.blocks.general.wetConcreteBlock
import net.cydhra.technocracy.astronautics.blocks.general.wetReinforcedConcreteBlock
import net.cydhra.technocracy.astronautics.capabilities.satellites.DefaultSatelliteOrbit
import net.cydhra.technocracy.astronautics.capabilities.satellites.ISatelliteOrbit
import net.cydhra.technocracy.astronautics.capabilities.satellites.SatelliteOrbitStorage
import net.cydhra.technocracy.astronautics.client.astronauticsCreativeTabs
import net.cydhra.technocracy.astronautics.client.model.concreteSprayer.ConcreteSprayerItemModel
import net.cydhra.technocracy.astronautics.items.general.concreteCanItem
import net.cydhra.technocracy.astronautics.items.general.concreteSprayerItem
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.items.general.ItemManager
import net.cydhra.technocracy.foundation.liquids.general.FluidManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager

open class CommonProxy {

    protected val blockManager = BlockManager(TCAstronautics.MODID, astronauticsCreativeTabs)
    protected val fluidManager = FluidManager(blockManager)
    protected val itemManager = ItemManager(TCAstronautics.MODID, astronauticsCreativeTabs)

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)
        MinecraftForge.EVENT_BUS.register(CapabilityEventBusHandler())

        blockManager.prepareBlocksForRegistration(scaffoldBlock)
        blockManager.prepareBlocksForRegistration(reinforcedConcreteBlock)
        blockManager.prepareBlocksForRegistration(wetConcreteBlock)
        blockManager.prepareBlocksForRegistration(wetReinforcedConcreteBlock)

        itemManager.prepareItemForRegistration(concreteSprayerItem, ConcreteSprayerItemModel())
        itemManager.prepareItemForRegistration(concreteCanItem)
    }

    open fun init() {
        CapabilityManager.INSTANCE.register(ISatelliteOrbit::class.java, SatelliteOrbitStorage(),
                ::DefaultSatelliteOrbit)
    }

    open fun postInit() {
    }
}