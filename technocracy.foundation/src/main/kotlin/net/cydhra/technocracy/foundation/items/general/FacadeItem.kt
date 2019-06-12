package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.blocks.PipeBlock
import net.cydhra.technocracy.foundation.blocks.general.pipe
import net.cydhra.technocracy.foundation.client.technocracyFacadeCreativeTab
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.cydhra.technocracy.foundation.util.FacadeStack
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.Block
import net.minecraft.block.BlockGlass
import net.minecraft.block.BlockStainedGlass
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
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
        if (creativeTab == this.creativeTab) {
            this.calculateSubTypes()
            itemStacks.addAll(this.facades)
        }
    }

    private fun calculateSubTypes() {
        if (facades.isEmpty()) {
            for (block in Block.REGISTRY) {
                try {
                    val item = Item.getItemFromBlock(block)
                    if (item == Items.AIR) {
                        continue
                    }

                    val blockSubItems = NonNullList.create<ItemStack>()
                    block.getSubBlocks(block.creativeTabToDisplayOn, blockSubItems)
                    for (subBlock in blockSubItems) {
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

        val hasTile = block.hasTileEntity(block.getStateFromMeta(metadata))
        val isGlass = block is BlockGlass || block is BlockStainedGlass || block.isTranslucent(block.getStateFromMeta(metadata))
        val isFullBlock = block.getStateFromMeta(metadata).isFullBlock

        val blockState = block.getStateFromMeta(metadata)

        if (blockState.renderType === EnumBlockRenderType.MODEL && !hasTile && isFullBlock) {
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