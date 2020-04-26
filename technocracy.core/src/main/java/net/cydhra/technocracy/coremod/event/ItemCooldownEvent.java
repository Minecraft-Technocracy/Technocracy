package net.cydhra.technocracy.coremod.event;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ItemCooldownEvent extends Event {
    public ItemCooldownEvent(Item item, int delay) {
        this.item = item;
        this.delay = delay;
    }

    Item item;
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
}
