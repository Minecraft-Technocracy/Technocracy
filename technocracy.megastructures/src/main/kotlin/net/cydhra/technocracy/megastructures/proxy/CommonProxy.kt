package net.cydhra.technocracy.megastructures.proxy

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager


open class CommonProxy {
    open fun preInit() {

    }

    open fun init() {
        MinecraftForge.EVENT_BUS.register(CapabilityEventBusHandler())
    }

    open fun postInit() {

    }
}