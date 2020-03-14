package net.cydhra.technocracy.optics

import net.cydhra.technocracy.optics.proxy.CommonProxy
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.oredict.OreDictionary
import org.apache.logging.log4j.Logger


@Mod(modid = TCOptics.MODID, name = TCOptics.NAME, version = TCOptics.VERSION,
        modLanguageAdapter = TCOptics.LANGUAGE_ADAPTER, dependencies = TCOptics.DEPENDENCIES)
@Mod.EventBusSubscriber(modid = TCOptics.MODID)
object TCOptics {

    /**
     * Module-internal constant for the forge mod identifier
     */
    const val MODID = "technocracy.optics"

    /**
     * Mod version
     */
    const val VERSION = "1.0"

    /**
     * Mod name used by forge
     */
    const val NAME = "Technocracy Optics"

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

    /**
     * A mutable shadow copy of certain parts of the ore dictionary. It exists, because the original ore dictionary
     * is broken and will not be fixed (as 1.12 is widely used but outdated. Don't ask me, I'm not making retarded
     * policies here)
     */
    private val oreDictionaryShadowCopy = mutableMapOf<String, ItemStack>()

    /**
     * A shadow copy of ore blocks from the ore dictionary.
     */
    val shadowOreDictionary: Map<String, ItemStack> = oreDictionaryShadowCopy

    @SidedProxy(
            serverSide = "net.cydhra.technocracy.optics.proxy.CommonProxy",
            clientSide = "net.cydhra.technocracy.optics.proxy.ClientProxy")
    lateinit var proxy: CommonProxy

    @Suppress("unused")
    @Mod.EventHandler
    fun preInit(@Suppress("UNUSED_PARAMETER") event: FMLPreInitializationEvent) {
        logger = FMLLog.log

        MinecraftForge.EVENT_BUS.register(proxy)

        // fill in vanilla ores, as those events already have been fired. (yes that is dumb)
        OreDictionary.getOreNames()
                .filter { it.startsWith("ore") }
                .forEach { name ->
                    oreDictionaryShadowCopy[name] = OreDictionary.getOres(name)[0]
                }

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
    @SubscribeEvent
    fun onOreRegister(event: OreDictionary.OreRegisterEvent) {
        if (event.name.startsWith("ore"))
            oreDictionaryShadowCopy.putIfAbsent(event.name, event.ore)
    }

}
