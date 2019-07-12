package net.cydhra.technocracy.megastructures

import net.cydhra.technocracy.megastructures.client.renderer.CustomSkyRenderer
import net.cydhra.technocracy.megastructures.proxy.CommonProxy
import net.cydhra.technocracy.megastructures.world.WrappedWorldProvider
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.apache.logging.log4j.Logger

@Mod(modid = TCMegaStructures.MODID, name = TCMegaStructures.NAME, version = TCMegaStructures.VERSION,
        modLanguageAdapter = TCMegaStructures.LANGUAGE_ADAPTER, dependencies = TCMegaStructures.DEPENDENCIES)
@Mod.EventBusSubscriber(modid = TCMegaStructures.MODID)
object TCMegaStructures {

    /**
     * Module-internal constant for the forge mod identifier
     */
    const val MODID = "technocracy.megastructures"

    /**
     * Mod version
     */
    const val VERSION = "1.0"

    /**
     * Mod name used by forge
     */
    const val NAME = "Technocracy Mega-Structures"

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

    @SidedProxy(
            serverSide = "net.cydhra.technocracy.megastructures.proxy.CommonProxy",
            clientSide = "net.cydhra.technocracy.megastructures.proxy.ClientProxy")
    lateinit var proxy: CommonProxy

    @Suppress("unused")
    @Mod.EventHandler
    fun preInit(@Suppress("UNUSED_PARAMETER") event: FMLPreInitializationEvent) {
        logger = FMLLog.log
        proxy.preInit()
    }

    @Suppress("unused")
    @Mod.EventHandler
    fun init(@Suppress("UNUSED_PARAMETER") event: FMLInitializationEvent) {
        proxy.init()
    }

    @Suppress("unused")
    @Mod.EventHandler
    fun postInit(@Suppress("UNUSED_PARAMETER") event: FMLPostInitializationEvent) {
        proxy.postInit()
    }

    @SubscribeEvent
    fun loadWorld(event: WorldEvent.Load) {
        event.world.provider.skyRenderer = CustomSkyRenderer
        event.world.provider = WrappedWorldProvider(event.world.provider)
    }

    @SubscribeEvent
    fun render(event: TickEvent.WorldTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            event.world.skylightSubtracted = 10
        }
    }
}
