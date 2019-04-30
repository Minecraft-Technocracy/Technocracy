package net.cydhra.technocracy.foundation

import net.cydhra.technocracy.foundation.proxy.CommonProxy
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger

@Mod(modid = TCFoundation.MODID, name = TCFoundation.NAME, version = TCFoundation.VERSION,
        modLanguageAdapter = TCFoundation.LANGUAGE_ADAPTER, dependencies = TCFoundation.DEPENDENCIES)
object TCFoundation {

    init {
        // enable the universal bucket for forge fluids. Must be done statically in here
        FluidRegistry.enableUniversalBucket()
    }

    /**
     * Module-internal constant for the forge mod identifier
     */
    const val MODID = "technocracy.foundation"

    /**
     * Mod version
     */
    const val VERSION = "1.0"

    /**
     * Mod name used by forge
     */
    const val NAME = "Technocracy Foundation"

    /**
     * The adapter responsible to load this mod class, as it is not a default java mod class
     */
    internal const val LANGUAGE_ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"

    /**
     * All forge mod hard-dependencies that must be provided
     */
    internal const val DEPENDENCIES = "required-after:forgelin;" +
            "required-after:cofhcore;" +
            "required-after:cofhworld;" +
            "required-after:zerocore"

    /**
     * Mod logger
     */
    lateinit var logger: Logger

    @SidedProxy(
            serverSide = "net.cydhra.technocracy.foundation.proxy.CommonProxy",
            clientSide = "net.cydhra.technocracy.foundation.proxy.ClientProxy")
    lateinit var proxy: CommonProxy

    @Suppress("unused")
    @EventHandler
    fun preInit(@Suppress("UNUSED_PARAMETER") event: FMLPreInitializationEvent) {
        logger = FMLLog.log
        proxy.preInit()
    }

    @Suppress("unused")
    @EventHandler
    fun init(@Suppress("UNUSED_PARAMETER") event: FMLInitializationEvent) {
        proxy.init()
    }

    @Suppress("unused")
    @EventHandler
    fun postInit(@Suppress("UNUSED_PARAMETER") event: FMLPostInitializationEvent) {
        proxy.postInit()
    }
}
