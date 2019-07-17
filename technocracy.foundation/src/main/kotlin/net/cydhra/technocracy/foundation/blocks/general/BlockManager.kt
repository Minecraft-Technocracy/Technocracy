package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.BaseLiquidBlock
import net.cydhra.technocracy.foundation.blocks.api.IBaseBlock
import net.cydhra.technocracy.foundation.client.model.AbstractCustomModel
import net.cydhra.technocracy.foundation.client.model.CustomModelProvider
import net.cydhra.technocracy.foundation.items.general.ItemSubBlock
import net.cydhra.technocracy.foundation.util.StateMapper
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


/**
 * A helper class that handles block related registration and management. All blocks that are to be registered shall
 * call [prepareBlocksForRegistration] during pre-initialization. The manager will wait for registration phase and
 * register them at the event bus. It furthermore handles additional block-related registration processes such as
 * [ItemBlock] registration, model and renderer registration and so on.
 */
class BlockManager(val defaultCreativeTab: CreativeTabs) {

    /**
     * A list that saves all blocks to be registered
     */
    private val blocksToRegister = mutableListOf<IBaseBlock>()

    private val customModels = mutableMapOf<String, IModel>()

    /**
     * Prepare a block for the registration happening per event later on.
     */
    fun prepareBlocksForRegistration(block: IBaseBlock): IBaseBlock {
        blocksToRegister += block
        return block
    }

    /**
     * Prepare a block for the registration happening per event later on, with custom Model.
     */
    fun prepareBlocksForRegistration(block: IBaseBlock, model: AbstractCustomModel): IBaseBlock {
        blocksToRegister += block
        val name = (block as Block).registryName!!.resourcePath
        customModels["models/block/$name"] = model.initModel("block", name)
        return block
    }

    /**
     * Register all previously [prepared][prepareBlocksForRegistration] blocks at the event bus.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onRegister(event: RegistryEvent.Register<Block>) {
        event.registry.registerAll(*blocksToRegister.map { it as Block }.map { it.apply { if (it.creativeTabToDisplayOn == null) it.setCreativeTab(defaultCreativeTab) } }.toTypedArray())
    }

    /**
     * Register the block items using the block's registry name
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onRegisterItems(event: RegistryEvent.Register<Item>) {
        event.registry.registerAll(*blocksToRegister.filter { it.generateItem }
                .filter { it !is BaseLiquidBlock }
                .map { it as Block }
                .map(::ItemSubBlock)
                .map { it.apply { it.registryName = it.block.registryName } }
                .toTypedArray())
    }

    /**
     * Register block items' models using the block's [IBaseBlock.modelLocation].
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onRegisterRenders(@Suppress("UNUSED_PARAMETER") event: ModelRegistryEvent) {
        blocksToRegister.filter { it.generateItem }
                .filter { it !is BaseLiquidBlock }
                .map { it as Block }
                .map(Item::getItemFromBlock)
                .forEach { item ->
                    val list = NonNullList.create<ItemStack>()
                    item.getSubItems(item.creativeTab!!, list)
                    for (subs in list) {
                        ModelLoader.setCustomModelResourceLocation(subs.item, subs.metadata,
                                ModelResourceLocation(((subs.item as ItemBlock).block as IBaseBlock).modelLocation, "inventory"))
                    }
                }
        registerCustomBlockModels()
    }

    /**
     * Called client-side during initialization. Registers colors for blocks and their [ItemBlock] instances.
     */
    fun registerBlockColors() {
        blocksToRegister.forEach { block ->
            if (block.colorMultiplier != null) {
                Minecraft.getMinecraft().blockColors.registerBlockColorHandler(block.colorMultiplier!!, block as Block)
                Minecraft.getMinecraft().itemColors.registerItemColorHandler(block.colorMultiplier!!, block)
            }
        }
    }

    private fun registerCustomBlockModels() {
        ModelLoaderRegistry.registerLoader(CustomModelProvider(customModels))
        blocksToRegister.filter { it is BaseLiquidBlock }
                .map { it as BaseLiquidBlock }
                .forEach { liquid ->
                    val stateMapper = StateMapper("fluid", liquid.modelLocation)
                    val item = Item.getItemFromBlock(liquid)
                    ModelBakery.registerItemVariants(item)
                    ModelLoader.setCustomMeshDefinition(item, stateMapper)
                    ModelLoader.setCustomStateMapper(liquid, stateMapper)
                }
    }

}