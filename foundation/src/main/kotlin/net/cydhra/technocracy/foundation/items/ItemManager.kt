package net.cydhra.technocracy.foundation.items

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

@Mod.EventBusSubscriber(modid = TCFoundation.MODID, value = [Side.CLIENT, Side.SERVER])
object ItemManager {

    object copperIngot : IngotItem("copper")
    object tinIngot : IngotItem("tin")
    object aluminumIngot : IngotItem("aluminum")
    object magnesiumIngot : IngotItem("magnesium")
    object lithiumIngot : IngotItem("lithium")
    object nickelIngot : IngotItem("nickel")
    object silverIngot : IngotItem("silver")

    /**
     * Initialize this manager
     */
    fun init() {

    }

    @SubscribeEvent
    @JvmStatic
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.register(copperIngot)
    }
}