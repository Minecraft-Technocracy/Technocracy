package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.client.technocracyFacadeCreativeTab
import net.cydhra.technocracy.foundation.content.blocks.PipeBlock
import net.cydhra.technocracy.foundation.content.tileentities.pipe.TileEntityPipe
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.cydhra.technocracy.foundation.util.facade.FacadeStack
import net.minecraft.block.Block
import net.minecraft.block.BlockGlass
import net.minecraft.block.BlockStainedGlass
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.concurrent.ThreadLocalRandom


class FacadeItem : BaseItem("facade") {

    val facades = mutableListOf<ItemStack>()

    init {
        this.creativeTab = technocracyFacadeCreativeTab
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        try {
            val facadeBlock = this.getFacadeFromStack(stack)
            if (!facadeBlock.stack.isEmpty) {
                return super.getItemStackDisplayName(stack) + " - " + facadeBlock.stack.displayName
            }
        } catch (ignored: Throwable) {
        }

        return super.getItemStackDisplayName(stack)
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing,
                           hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val block = worldIn.getBlockState(pos).block
        val itemStack = player.getHeldItem(hand)

        if (block is PipeBlock) {
            val tile = worldIn.getTileEntity(pos)!! as TileEntityPipe

            if (tile.addFacadeOnSide(itemStack, facing)) {
                itemStack.shrink(1)
                return EnumActionResult.SUCCESS
            }
        }

        return EnumActionResult.FAIL
    }

    fun getRandomFacade(): ItemStack {
        calculateSubTypes()
        return facades[ThreadLocalRandom.current().nextInt(facades.size - 1)]
    }

    override fun getSubItems(creativeTab: CreativeTabs, itemStacks: NonNullList<ItemStack>) {
        if (isInCreativeTab(creativeTab)) {
            this.calculateSubTypes()
            itemStacks.addAll(this.facades)
        }
    }

    private fun calculateSubTypes() {
        if (facades.isEmpty()) {
            for (block in Block.REGISTRY) {
                try {
                    val item = getItemFromBlock(block)
                    if (item == Items.AIR) {
                        continue
                    }

                    val blockSubItems = NonNullList.create<ItemStack>()
                    for (tab in CreativeTabs.CREATIVE_TAB_ARRAY)
                        block.getSubBlocks(tab, blockSubItems)
                    for (subBlock in blockSubItems.distinctBy { it.serializeNBT() }) {
                        val facade = this.createFacadeForItem(subBlock)
                        if (!facade.isEmpty) {
                            facades.add(facade)
                        }
                    }
                } catch (t: Throwable) {
                }
            }
        }
    }

    fun getFacadeFromStack(stack: ItemStack): FacadeStack {
        val nbt = stack.tagCompound ?: return FacadeStack(ItemStack.EMPTY, false)

        val meta = nbt.getInteger("facade_meta")
        val name = ResourceLocation(nbt.getString("facade_name"))
        val transparent = nbt.getBoolean("facade_transparent")

        val item = Item.REGISTRY.getObject(name) ?: return FacadeStack(ItemStack.EMPTY, false)

        return FacadeStack(ItemStack(item, 1, meta), transparent)
    }

    fun createFacadeForItem(stack: ItemStack): ItemStack {
        if (stack.isEmpty) {
            return ItemStack.EMPTY
        }

        val block = Block.getBlockFromItem(stack.item)
        if (block === Blocks.AIR) {
            return ItemStack.EMPTY
        }

        val metadata = stack.item.getMetadata(stack.itemDamage)

        val blockState = block.getStateFromMeta(metadata)

        val hasTile = block.hasTileEntity(blockState)
        val isGlass = block is BlockGlass || block is BlockStainedGlass
        val isFullCube = block.isFullCube(blockState)
        val model = blockState.renderType == EnumBlockRenderType.MODEL;

        if ((isFullCube || isGlass) && model && !hasTile) {
            val `is` = ItemStack(this)
            val data = NBTTagCompound()
            data.setString("facade_name", stack.item.registryName!!.toString())
            data.setInteger("facade_meta", stack.itemDamage)
            data.setBoolean("facade_transparent", isGlass)
            `is`.tagCompound = data
            return `is`
        }
        return ItemStack.EMPTY
    }
}