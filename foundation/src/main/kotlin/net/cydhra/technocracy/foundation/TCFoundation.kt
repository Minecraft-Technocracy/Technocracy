package net.cydhra.technocracy.foundation

import net.cydhra.technocracy.foundation.items.ItemManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger

@Mod(modid = TCFoundation.MODID, name = TCFoundation.NAME, version = TCFoundation.VERSION,
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object TCFoundation {

    private lateinit var logger: Logger

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

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        ItemManager.init()
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {

    }
}
