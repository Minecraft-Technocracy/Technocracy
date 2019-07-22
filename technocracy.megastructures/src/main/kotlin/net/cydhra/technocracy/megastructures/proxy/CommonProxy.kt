package net.cydhra.technocracy.megastructures.proxy

import net.cydhra.technocracy.megastructures.dyson.DysonSphereController
import net.minecraftforge.common.MinecraftForge


open class CommonProxy {
    open fun preInit() {

    }

    open fun init() {
        MinecraftForge.EVENT_BUS.register(CapabilityEventBusHandler())
    }

    open fun postInit() {
        DysonSphereController.initialize()
    }
}