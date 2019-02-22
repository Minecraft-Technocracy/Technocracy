
package net.cydhra.technocracy.foundation.items

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

/**
 * This Manager object is responsible for collecting items and registering them in registering phase.
 *
 * @see ItemManager.prepareItemForRegistration
 */
@Mod.EventBusSubscriber(modid = TCFoundation.MODID, value = [Side.CLIENT, Side.SERVER])
object ItemManager {

    /**
     * Items scheduled for registering
     */
    private val itemsToRegister = mutableListOf<BaseItem>()

    /**
     * Schedule an item for registration. Registration will be done, as soon as the registration event marks
     * registration phase.
     */
    fun prepareItemForRegistration(item: BaseItem) {
        this.itemsToRegister += item
    }

    @Suppress("unused")
    @SubscribeEvent
    @JvmStatic
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(*this.itemsToRegister.toTypedArray())

        this.itemsToRegister.clear()
    }
}