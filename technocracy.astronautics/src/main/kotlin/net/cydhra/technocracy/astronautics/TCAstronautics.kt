package net.cydhra.technocracy.astronautics

import net.cydhra.technocracy.astronautics.client.renderer.CustomSkyRenderer
import net.cydhra.technocracy.astronautics.content.commands.DysonSphereCommand
import net.cydhra.technocracy.astronautics.content.world.WrappedWorldProvider
import net.cydhra.technocracy.astronautics.proxy.CommonProxy
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.Logger


@Mod(modid = TCAstronautics.MODID, name = TCAstronautics.NAME, version = TCAstronautics.VERSION,
        modLanguageAdapter = TCAstronautics.LANGUAGE_ADAPTER, dependencies = TCAstronautics.DEPENDENCIES)
@Mod.EventBusSubscriber(modid = TCAstronautics.MODID)
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

    @SidedProxy(
            serverSide = "net.cydhra.technocracy.astronautics.proxy.CommonProxy",
            clientSide = "net.cydhra.technocracy.astronautics.proxy.ClientProxy")
    lateinit var proxy: CommonProxy

    @Suppress("unused")
    @Mod.EventHandler
    fun preInit(@Suppress("UNUSED_PARAMETER") event: FMLPreInitializationEvent) {
        logger = FMLLog.log

        MinecraftForge.EVENT_BUS.register(proxy)
        proxy.initializeProxy()
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

    @Suppress("unused")
    @Mod.EventHandler
    fun serverStarting(start: FMLServerStartingEvent) {
        start.registerServerCommand(DysonSphereCommand())
    }

    @Suppress("unused")
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun loadWorldClient(event: WorldEvent.Load) {
        event.world.provider.skyRenderer = CustomSkyRenderer
    }

    @Suppress("unused")
    @SubscribeEvent
    fun loadWorld(event: WorldEvent.Load) {
        event.world.provider = WrappedWorldProvider(event.world.provider)
    }

    @Suppress("unused")
    @SubscribeEvent
    fun render(event: TickEvent.WorldTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            //event.world.skylightSubtracted = 10
        }
    }
}
