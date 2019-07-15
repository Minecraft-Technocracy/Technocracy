package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.astronautics.blocks.general.scaffoldBlock
import net.cydhra.technocracy.astronautics.capabilities.satellites.DefaultSatelliteOrbit
import net.cydhra.technocracy.astronautics.capabilities.satellites.ISatelliteOrbit
import net.cydhra.technocracy.astronautics.capabilities.satellites.SatelliteOrbitStorage
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.blocks.general.heatExchangerControllerBlock
import net.cydhra.technocracy.foundation.items.general.ItemManager
import net.cydhra.technocracy.foundation.liquids.general.FluidManager
import net.cydhra.technocracy.foundation.pipes.Network
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager

open class CommonProxy {

    protected val blockManager = BlockManager()
    protected val fluidManager = FluidManager(blockManager)
    protected val itemManager = ItemManager()

    open fun preInit() {
        MinecraftForge.EVENT_BUS.register(blockManager)
        MinecraftForge.EVENT_BUS.register(fluidManager)
        MinecraftForge.EVENT_BUS.register(itemManager)
        MinecraftForge.EVENT_BUS.register(CapabilityEventBusHandler())

        blockManager.prepareBlocksForRegistration(scaffoldBlock)
    }

    open fun init() {
        CapabilityManager.INSTANCE.register(ISatelliteOrbit::class.java, SatelliteOrbitStorage(),
                ::DefaultSatelliteOrbit)
    }

    open fun postInit() {

    }
}