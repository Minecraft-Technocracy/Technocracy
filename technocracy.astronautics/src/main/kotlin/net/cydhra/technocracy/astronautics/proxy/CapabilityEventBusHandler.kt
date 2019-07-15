package net.cydhra.technocracy.astronautics.proxy

import net.cydhra.technocracy.astronautics.TCAstronautics
import net.cydhra.technocracy.astronautics.capabilities.satellites.SatelliteOrbitProvider
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldServer
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * Utility class listening to events for the capability system
 */
@Suppress("unused")
class CapabilityEventBusHandler {

    /**
     * Attach satellite orbits to all chunks
     */
    @SubscribeEvent
    fun onAttachChunkCapabilities(event: AttachCapabilitiesEvent<Chunk>) {
        event.addCapability(ResourceLocation(TCAstronautics.MODID, "satellites"), SatelliteOrbitProvider(event.getObject()))
    }

    /**
     * Tick all satellite orbits
     */
    @SubscribeEvent
    fun onTick(event: TickEvent.WorldTickEvent) {
        if (event.world.isRemote)
            return

        (event.world as WorldServer).chunkProvider.loadedChunks.forEach { chunk ->
            chunk.getCapability(SatelliteOrbitProvider.CAPABILITY_SATELLITE_ORBIT, null)?.tick()
        }
    }
}