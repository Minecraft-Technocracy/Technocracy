package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.client.model.AbstractCustomModel
import net.cydhra.technocracy.foundation.client.model.CustomModelProvider
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.oredict.OreDictionary

/**
 * This Manager object is responsible for collecting items and registering them in registering phase.
 *
 * @see ItemManager.prepareItemForRegistration
 */
class ItemManager(val modName: String,val defaultCreativeTab: CreativeTabs) {

    /**
     * Items scheduled for registering
     */
    private val itemsToRegister = mutableListOf<BaseItem>()

    private val customModels = mutableMapOf<String, IModel>()

    /**
     * Schedule an item for registration. Registration will be done, as soon as the registration event marks
     * registration phase.
     */
    fun prepareItemForRegistration(item: BaseItem) {
        itemsToRegister += item
    }

    /**
     * Schedule an item for registration. Registration will be done, as soon as the registration event marks
     * registration phase, with custom model
     */
    fun prepareItemForRegistration(item: BaseItem, model: AbstractCustomModel) {
        itemsToRegister += item
        val name = item.registryName!!.resourcePath
        customModels["models/item/$name"] = model.initModel(modName, "item", name)
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(*itemsToRegister.map { it.apply { if (it.creativeTab == null) it.creativeTab = defaultCreativeTab } }.toTypedArray())

    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerRenders(@Suppress("UNUSED_PARAMETER") event: ModelRegistryEvent) {
        ModelLoaderRegistry.registerLoader(CustomModelProvider(customModels, modName))

        itemsToRegister.forEach(this::registerItemRender)
        itemsToRegister
                .filter { it.oreDictName != null }
                .forEach { OreDictionary.registerOre(it.oreDictName, it) }
    }

    /**
     * Must be called client side in initialization phase
     */
    fun registerItemColors() {
        itemsToRegister.forEach { item ->
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
        val list = NonNullList.create<ItemStack>()
        item.getSubItems(item.creativeTab!!, list)
        //need to get all item variants
        item.getSubItems(CreativeTabs.SEARCH, list)
        for (subs in list) {
            registerItemRender(item, subs.metadata)
        }
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