package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.cydhra.technocracy.foundation.content.blocks.PipeBlock
import net.cydhra.technocracy.foundation.content.blocks.pipe
import net.cydhra.technocracy.foundation.content.tileentities.pipe.TileEntityPipe
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory.BLOCKS
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class PipeItem : BaseItem("pipe_item") {

    init {
        hasSubtypes = true
        maxDamage = 0
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, posIn: BlockPos, hand: EnumHand, facing: EnumFacing,
                           hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        var pos = posIn
        val state = worldIn.getBlockState(pos)
        var block = state.block

        val stack = player.getHeldItem(hand)

        if (!block.isReplaceable(worldIn, pos)) {
            var ofsetted = false
            if (block !is PipeBlock || player.isSneaking) {
                pos = pos.offset(facing)
                block = worldIn.getBlockState(pos).block
                ofsetted = true
            }
            if (block is PipeBlock) {
                val tile = worldIn.getTileEntity(pos)!! as TileEntityPipe

                val thisPipeType = PipeType[stack.metadata]

                if (!tile.hasPipeType(thisPipeType)) {
                    tile.addPipeType(thisPipeType)
                    tile.markForUpdate()

                    val pipeState = worldIn.getBlockState(pos)
                    val soundtype = pipeState.block.getSoundType(pipeState, worldIn, pos, player)
                    worldIn.playSound(player, pos, soundtype.placeSound, BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f)
                    stack.shrink(1)

                    return EnumActionResult.SUCCESS
                } else if(!ofsetted) {
                    pos = pos.offset(facing)
                }
            }
        }

        val canedit = player.canPlayerEdit(pos, facing, stack)
        val mayplace = worldIn.mayPlace(pipe, pos, false, facing, null)

        return if (!stack.isEmpty && canedit && mayplace) {
            var pipeState = pipe.defaultState

            if (placeBlockAt(stack, player, worldIn, pos, facing, hitX, hitY, hitZ, pipeState)) {
                pipeState = worldIn.getBlockState(pos)
                val soundtype = pipeState.block.getSoundType(pipeState, worldIn, pos, player)
                worldIn.playSound(player, pos, soundtype.placeSound, BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f)
                stack.shrink(1)
            }

            EnumActionResult.SUCCESS
        } else {
            EnumActionResult.FAIL
        }
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    fun placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState): Boolean {
        if (!world.setBlockState(pos, newState, 11)) return false

        val state = world.getBlockState(pos)
        if (state.block === pipe) {
            ItemBlock.setTileEntityNBT(world, player, pos, stack)
            pipe.onBlockPlacedBy(world, pos, state, player, stack)

            val tile = world.getTileEntity(pos)
            if (tile != null)
                (tile as TileEntityPipe).addPipeType(PipeType[stack.metadata])

            if (player is EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger(player, pos, stack)
        }

        return true
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return super.getUnlocalizedName() + "." + PipeType[stack.metadata].unlocalizedName
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (this.isInCreativeTab(tab)) {
            for (i in 0 until PipeType.values().size) {
                items.add(ItemStack(this, 1, i))
            }
        }
    }
}