package net.cydhra.technocracy.megastructures.proxy

import net.minecraft.world.World
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


class CapabilityEventBusHandler {
    @SubscribeEvent
    fun onAttachWorldCapabilities(event: AttachCapabilitiesEvent<World>) {
    }
}