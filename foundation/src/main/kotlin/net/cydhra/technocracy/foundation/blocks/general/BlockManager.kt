package net.cydhra.technocracy.foundation.blocks.general

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
 * A manager for blocks added by the mod
 */
@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
object BlockManager {

    private val blocksToRegister = mutableListOf<IBaseBlock>()

    /**
     * Prepare a block for the registration happening per event later on.
     */
    fun prepareBlocksForRegistration(block: IBaseBlock): IBaseBlock {
        blocksToRegister += block
        return block
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onRegister(event: RegistryEvent.Register<Block>) {
        event.registry.registerAll(*blocksToRegister.map { it as Block }.toTypedArray())
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onRegisterItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(*blocksToRegister
                .map { it as Block }
                .map(::ItemBlock)
                .map { it.apply { it.registryName = it.block.registryName } }
                .toTypedArray())
    }

    @Suppress("unused")
    @SubscribeEvent
    fun onRegisterRenders(event: ModelRegistryEvent) {
        blocksToRegister
                .map { it as Block }
                .map(Item::getItemFromBlock)
                .forEach(this::registerRender)
    }

    /**
     * Called client-side during initialization. Registers colors for blocks and their [ItemBlock] instances.
     */
    fun registerBlockColors() {
        blocksToRegister.forEach { block ->
            if (block.colorMultiplier != null) {
                Minecraft.getMinecraft().blockColors.registerBlockColorHandler(block.colorMultiplier, block as Block)
                Minecraft.getMinecraft().itemColors.registerItemColorHandler(block.colorMultiplier, block)
            }
        }
    }

    /**
     * Convenience function to preInit a custom model resource for an ItemBlock.
     *
     * @param item an instance of [Item]. Note that the function asserts that it is actually an [ItemBlock]. It is
     * just out of convenience an [Item] to be used as function reference in stream calls.
     */
    private fun registerRender(item: Item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(((item as ItemBlock).block as
                AbstractBaseBlock).modelLocation, "inventory"))
    }
}