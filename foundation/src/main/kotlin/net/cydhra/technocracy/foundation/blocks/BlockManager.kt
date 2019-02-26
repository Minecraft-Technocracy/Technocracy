package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


/**
 *
 */
@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
object BlockManager {

    private val blocksToRegister = mutableListOf<BaseBlock>()

    fun prepareBlocksForRegistration(block: BaseBlock) {
        blocksToRegister += block
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onRegister(event: RegistryEvent.Register<Block>) {
        event.registry.registerAll(*blocksToRegister.toTypedArray())
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onRegisterItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(*blocksToRegister
                .map(::ItemBlock)
                .map { it.apply { it.registryName = it.block.registryName } }
                .toTypedArray())
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onRegisterRenders(event: ModelRegistryEvent) {
        this.blocksToRegister.map(Item::getItemFromBlock).forEach(this::registerRender)
    }

    fun registerBlockColors() {
        this.blocksToRegister.forEach { block ->
            if (block.colorMultiplier != null)
                Minecraft.getMinecraft().blockColors.registerBlockColorHandler(block.colorMultiplier, block)
        }
    }

    private fun registerRender(item: Item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(((item as ItemBlock).block as
                BaseBlock).modelLocation, "inventory"))
    }
}