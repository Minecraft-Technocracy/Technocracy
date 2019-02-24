package net.cydhra.technocracy.foundation.items

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * This Manager object is responsible for collecting items and registering them in registering phase.
 *
 * @see ItemManager.prepareItemForRegistration
 */
@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
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

    }

    @Suppress("unused")
    @SubscribeEvent
    @JvmStatic
    fun registerRenders(event: ModelRegistryEvent) {
        this.itemsToRegister.forEach(this::registerItemRender)
    }

    /**
     * Must be called client side in initialization phase
     */
    fun registerItemColors() {
        this.itemsToRegister.forEach { item ->
            if (item.itemColor != null)
                Minecraft.getMinecraft().itemColors.registerItemColorHandler(item.itemColor, item)
        }
    }

    /**
     * Register a custom model resource location for an item.
     *
     * @param item a [BaseItem] instance
     */
    private fun registerItemRender(item: BaseItem) {
        this.registerItemRender(item, 0)
    }

    /**
     * Register a custom model resource location for an item.
     *
     * @param item a [BaseItem] instance
     * @param metadata the item metadata which uses this model resource
     */
    private fun registerItemRender(item: BaseItem, metadata: Int) {
        ModelLoader.setCustomModelResourceLocation(item, metadata,
                ModelResourceLocation(item.modelLocation, "inventory"))
    }
}