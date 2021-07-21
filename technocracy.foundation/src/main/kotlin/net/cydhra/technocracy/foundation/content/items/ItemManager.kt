package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.api.items.TCItem
import net.cydhra.technocracy.foundation.client.model.AbstractCustomModel
import net.cydhra.technocracy.foundation.client.model.CustomModelProvider
import net.cydhra.technocracy.foundation.content.blocks.color.BlockColorDelegator
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
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary

/**
 * This Manager object is responsible for collecting items and registering them in registering phase.
 *
 * @see ItemManager.prepareItemForRegistration
 */
class ItemManager(val modName: String, val defaultCreativeTab: CreativeTabs) {

    /**
     * Items scheduled for registering
     */
    private val itemsToRegister = mutableListOf<TCItem>()

    @SideOnly(Side.CLIENT)
    private lateinit var customModels: MutableMap<String, IModel>

    @SideOnly(Side.CLIENT)
    fun initClient() {
        customModels = mutableMapOf<String, IModel>()
    }

    /**
     * Schedule an item for registration. Registration will be done, as soon as the registration event marks
     * registration phase.
     */
    fun <T> prepareItemForRegistration(item: T) where T : TCItem, T : Item {
        itemsToRegister += item
    }

    /**
     * Schedule an item for registration. Registration will be done, as soon as the registration event marks
     * registration phase, with custom model
     */
    @SideOnly(Side.CLIENT)
    fun linkItemToModel(item: BaseItem, model: AbstractCustomModel) {
        val name = item.registryName!!.resourcePath
        customModels["models/item/$name"] = model.initModel(modName, "item", name)
    }

    @Suppress("unused")
    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(*itemsToRegister.map {
            (it as Item).apply { if (it.creativeTab == null) it.creativeTab = defaultCreativeTab }
        }.toTypedArray())
        itemsToRegister
                .filter { it.oreDictName != null }
                .forEach { OreDictionary.registerOre(it.oreDictName, it as Item) }
    }

    @SideOnly(Side.CLIENT)
    @Suppress("unused")
    @SubscribeEvent
    fun registerRenders(@Suppress("UNUSED_PARAMETER") event: ModelRegistryEvent) {
        ModelLoaderRegistry.registerLoader(CustomModelProvider(customModels, modName))

        itemsToRegister.forEach(this::registerItemRender)
    }

    /**
     * Must be called client side in initialization phase
     */
    @SideOnly(Side.CLIENT)
    fun registerItemColors() {
        itemsToRegister.forEach { item ->
            if (item.itemColor != null)
                Minecraft.getMinecraft()
                        .itemColors.registerItemColorHandler(BlockColorDelegator(item.itemColor!!), item as Item)
        }
    }

    /**
     * Register a custom model resource location for an item.
     *
     * @param item a [BaseItem] instance
     */
    @SideOnly(Side.CLIENT)
    private fun registerItemRender(item: TCItem) {
        val list = NonNullList.create<ItemStack>()
        (item as Item).getSubItems(item.creativeTab!!, list)
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
    @SideOnly(Side.CLIENT)
    private fun registerItemRender(item: TCItem, metadata: Int) {
        ModelLoader.setCustomModelResourceLocation(item as Item, metadata,
                ModelResourceLocation(item.modelLocation, "inventory"))
    }
}