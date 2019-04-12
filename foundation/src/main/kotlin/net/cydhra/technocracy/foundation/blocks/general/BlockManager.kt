package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.general.BlockManager.prepareBlocksForRegistration
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
 * A helper class that handles block related registration and management. All blocks that are to be registered shall
 * call [prepareBlocksForRegistration] during pre-initialization. The manager will wait for registration phase and
 * register them at the event bus. It furthermore handles additional block-related registration processes such as
 * [ItemBlock] registration, model and renderer registration and so on.
 */
@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
object BlockManager {

    /**
     * A list that saves all blocks to be registered
     */
    private val blocksToRegister = mutableListOf<IBaseBlock>()

    /**
     * Prepare a block for the registration happening per event later on.
     */
    fun prepareBlocksForRegistration(block: IBaseBlock): IBaseBlock {
        blocksToRegister += block
        return block
    }

    /**
     * Register all previously [prepared][prepareBlocksForRegistration] blocks at the event bus.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onRegister(event: RegistryEvent.Register<Block>) {
        event.registry.registerAll(*blocksToRegister.map { it as Block }.toTypedArray())
    }

    /**
     * Register the block items using the block's registry name
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onRegisterItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(*blocksToRegister
                .map { it as Block }
                .map(::ItemBlock)
                .map { it.apply { it.registryName = it.block.registryName } }
                .toTypedArray())
    }

    /**
     * Register block items' models using the block's [IBaseBlock.modelLocation].
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onRegisterRenders(@Suppress("UNUSED_PARAMETER") event: ModelRegistryEvent) {
        blocksToRegister
                .map { it as Block }
                .map(Item::getItemFromBlock)
                .forEach { item ->
                    ModelLoader.setCustomModelResourceLocation(item as ItemBlock, 0,
                            ModelResourceLocation(((item).block as IBaseBlock).modelLocation, "inventory"))
                }
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

}