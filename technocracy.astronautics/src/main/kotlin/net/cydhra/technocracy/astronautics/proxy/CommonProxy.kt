package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.astronautics.blocks.general.reinforcedConcrete
import net.cydhra.technocracy.astronautics.blocks.general.scaffoldBlock
import net.cydhra.technocracy.astronautics.blocks.general.wetConcrete
import net.cydhra.technocracy.astronautics.blocks.general.wetReinforcedConcrete
import net.cydhra.technocracy.astronautics.capabilities.satellites.DefaultSatelliteOrbit
import net.cydhra.technocracy.astronautics.capabilities.satellites.ISatelliteOrbit
import net.cydhra.technocracy.astronautics.capabilities.satellites.SatelliteOrbitStorage
import net.cydhra.technocracy.astronautics.client.astronauticsCreativeTabs
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.items.general.ItemManager
import net.cydhra.technocracy.foundation.liquids.general.FluidManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager

open class CommonProxy {

    protected val blockManager = BlockManager(astronauticsCreativeTabs)
    protected val fluidManager = FluidManager(blockManager)
    protected val itemManager = ItemManager(astronauticsCreativeTabs)

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)
        MinecraftForge.EVENT_BUS.register(CapabilityEventBusHandler())

        blockManager.prepareBlocksForRegistration(scaffoldBlock)
        blockManager.prepareBlocksForRegistration(reinforcedConcrete)
        blockManager.prepareBlocksForRegistration(wetConcrete)
        blockManager.prepareBlocksForRegistration(wetReinforcedConcrete)
    }

    open fun init() {
        CapabilityManager.INSTANCE.register(ISatelliteOrbit::class.java, SatelliteOrbitStorage(),
                ::DefaultSatelliteOrbit)
    }

    open fun postInit() {
    }
}