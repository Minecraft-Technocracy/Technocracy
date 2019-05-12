package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.astronautics.TCAstronautics
import net.cydhra.technocracy.astronautics.capabilities.satellites.SatelliteOrbitProvider
import net.minecraft.util.ResourceLocation
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Utility class listening to events for the capability system
 */
class CapabilityEventBusHandler {

    @SubscribeEvent
    fun onAttachChunkCapabilities(event: AttachCapabilitiesEvent<Chunk>) {
        event.addCapability(ResourceLocation(TCAstronautics.MODID, "satellites"), SatelliteOrbitProvider(event.getObject()))
    }
}