package net.cydhra.technocracy.megastructures

import net.cydhra.technocracy.megastructures.client.renderer.CustomSkyRenderer
import net.cydhra.technocracy.megastructures.world.WrappedWorldProvider
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

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
    const val VERSION = "@VERSION@"

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

    @SubscribeEvent
    fun loadWorld(event: WorldEvent.Load) {
        event.world.provider.skyRenderer = CustomSkyRenderer()
        event.world.provider = WrappedWorldProvider(event.world.provider)
    }

    @SubscribeEvent
    fun render(event: TickEvent.WorldTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            event.world.skylightSubtracted = 10
        }
    }
}
