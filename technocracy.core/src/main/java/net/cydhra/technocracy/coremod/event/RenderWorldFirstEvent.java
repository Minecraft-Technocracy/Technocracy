package net.cydhra.technocracy.coremod.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderWorldFirstEvent extends Event {

    private final float partialTicks;

    public RenderWorldFirstEvent(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }
}
