package net.cydhra.technocracy.foundation

import net.cydhra.technocracy.foundation.proxy.CommonProxy
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger

@Mod(modid = TCFoundation.MODID, name = TCFoundation.NAME, version = TCFoundation.VERSION,
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object TCFoundation {

    /**
     * Module-internal constant for the forge mod identifier
     */
    internal const val MODID = "##MOD.ID"

    /**
     * Mod version
     */
    const val VERSION = "##MOD.VERSION"

    /**
     * Mod name used by forge
     */
    const val NAME = "Technocracy Foundation"

    /**
     * Mod logger
     */
    lateinit var logger: Logger

    @SidedProxy(
            serverSide = "net.cydhra.technocracy.foundation.proxy.CommonProxy",
            clientSide = "net.cydhra.technocracy.foundation.proxy.ClientProxy")
    lateinit var proxy: CommonProxy

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = FMLLog.log
        proxy.preInit()
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init()
    }

    @EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        proxy.postInit()
    }
}
