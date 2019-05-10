package net.cydhra.technocracy.astronautics

import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger


@Mod(modid = TCAstronautics.MODID, name = TCAstronautics.NAME, version = TCAstronautics.VERSION,
        modLanguageAdapter = TCAstronautics.LANGUAGE_ADAPTER, dependencies = TCAstronautics.DEPENDENCIES)
object TCAstronautics {

    /**
     * Module-internal constant for the forge mod identifier
     */
    const val MODID = "technocracy.astronautics"

    /**
     * Mod version
     */
    const val VERSION = "1.0"

    /**
     * Mod name used by forge
     */
    const val NAME = "Technocracy Astronautics"

    /**
     * The adapter responsible to load this mod class, as it is not a default java mod class
     */
    internal const val LANGUAGE_ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"

    /**
     * All forge mod hard-dependencies that must be provided
     */
    internal const val DEPENDENCIES = "required-after:forgelin;" +
            "required-after:technocracy.foundation"

    /**
     * Mod logger
     */
    lateinit var logger: Logger

    @Suppress("unused")
    @Mod.EventHandler
    fun preInit(@Suppress("UNUSED_PARAMETER") event: FMLPreInitializationEvent) {
        logger = FMLLog.log
    }
}
