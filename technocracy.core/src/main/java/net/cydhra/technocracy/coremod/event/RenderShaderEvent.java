package net.cydhra.technocracy.coremod.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderShaderEvent extends Event {
    private float partialTicks;

    public RenderShaderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
