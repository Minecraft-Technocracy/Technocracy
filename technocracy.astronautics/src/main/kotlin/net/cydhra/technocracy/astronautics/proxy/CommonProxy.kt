package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.astronautics.blocks.general.scaffoldBlock
import net.cydhra.technocracy.astronautics.capabilities.satellites.DefaultSatelliteOrbit
import net.cydhra.technocracy.astronautics.capabilities.satellites.ISatelliteOrbit
import net.cydhra.technocracy.astronautics.capabilities.satellites.SatelliteOrbitStorage
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.blocks.general.heatExchangerControllerBlock
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager

open class CommonProxy {

    open fun preInit() {
        BlockManager.prepareBlocksForRegistration(scaffoldBlock)
    }

    open fun init() {
        CapabilityManager.INSTANCE.register(ISatelliteOrbit::class.java, SatelliteOrbitStorage(),
                ::DefaultSatelliteOrbit)
        MinecraftForge.EVENT_BUS.register(CapabilityEventBusHandler())
    }

    open fun postInit() {

    }
}