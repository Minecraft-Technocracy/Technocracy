package net.cydhra.technocracy.coremod.event;

import net.minecraft.item.Item;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.CooldownTrackerServer;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

public class ItemCooldownEvent extends Event {
    public ItemCooldownEvent(CooldownTracker tracker, Item item, int delay) {
        this.item = item;
        this.delay = delay;
        this.tracker = tracker;
    }

    Item item;
    CooldownTracker tracker;
    int delay;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Item getItem() {
        return item;
    }

    public CooldownTracker getTracker() {
        return tracker;
    }
}
