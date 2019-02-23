package net.cydhra.technocracy.foundation

import net.cydhra.technocracy.foundation.proxy.ClientProxy
import net.cydhra.technocracy.foundation.proxy.CommonProxy
import net.cydhra.technocracy.foundation.proxy.ISidedProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
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
    private lateinit var logger: Logger

    private lateinit var proxy: ISidedProxy

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        proxy = if (event.side == Side.CLIENT) ClientProxy() else CommonProxy()

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
