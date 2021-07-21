package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.api.blocks.IBaseBlock
import net.cydhra.technocracy.foundation.client.model.AbstractCustomModel
import net.cydhra.technocracy.foundation.client.model.CustomModelProvider
import net.cydhra.technocracy.foundation.content.blocks.color.BlockColorDelegator
import net.cydhra.technocracy.foundation.content.items.ItemSubBlock
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
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary


/**
 * A helper class that handles block related registration and management. All blocks that are to be registered shall
 * call [prepareBlocksForRegistration] during pre-initialization. The manager will wait for registration phase and
 * register them at the event bus. It furthermore handles additional block-related registration processes such as
 * [ItemBlock] registration, model and renderer registration and so on.
 */
class BlockManager(val modName: String, val defaultCreativeTab: CreativeTabs) {

    /**
     * A list that saves all blocks to be registered
     */
    private val blocksToRegister = mutableListOf<IBaseBlock>()

    @SideOnly(Side.CLIENT)
    private lateinit var customModels: MutableMap<String, IModel>

    @SideOnly(Side.CLIENT)
    fun initClient() {
        customModels = mutableMapOf<String, IModel>()
    }

    /**
     * Prepare a block for the registration happening per event later on.
     */
    fun prepareBlocksForRegistration(block: IBaseBlock): IBaseBlock {
        blocksToRegister += block
        return block
    }

    @SideOnly(Side.CLIENT)
    fun linkBlockToModel(block: IBaseBlock, model: AbstractCustomModel) {
        val name = (block as Block).registryName!!.resourcePath
        customModels["models/block/$name"] = model.initModel(modName, "block", name)
    }

    /**
     * Register all previously [prepared][prepareBlocksForRegistration] blocks at the event bus.
     */
    @Suppress("unused")
    @SubscribeEvent
    fun onRegister(event: RegistryEvent.Register<Block>) {
        event.registry.registerAll(*blocksToRegister.map { it as Block }.map {
            it.apply {
                @Suppress("SENSELESS_COMPARISON") // this is an actually nullable platform type
                if (it.creativeTabToDisplayOn == null) it.setCreativeTab(defaultCreativeTab)
            }
        }.toTypedArray())
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

        blocksToRegister
                .filterIsInstance<AbstractBaseBlock>()
                .filter { it.oreDictionaryName != null }
                .forEach {
                    OreDictionary.registerOre(it.oreDictionaryName, it)
                }
    }

    /**
     * Register block items' models using the block's [IBaseBlock.modelLocation].
     */
    @SideOnly(Side.CLIENT)
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
                    item.getSubItems(CreativeTabs.SEARCH, list)
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
                val handler = BlockColorDelegator(block.colorMultiplier!!)
                Minecraft.getMinecraft().blockColors.registerBlockColorHandler(handler, block as Block)
                Minecraft.getMinecraft().itemColors.registerItemColorHandler(handler, block)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private fun registerCustomBlockModels() {
        ModelLoaderRegistry.registerLoader(CustomModelProvider(customModels, modName))
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