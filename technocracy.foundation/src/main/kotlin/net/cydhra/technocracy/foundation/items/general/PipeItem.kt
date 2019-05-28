package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.blocks.PipeBlock
import net.cydhra.technocracy.foundation.blocks.general.pipe
import net.cydhra.technocracy.foundation.client.renderer.tileEntity.PipeRenderer
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class PipeItem : BaseItem("pipe") {

    init {
        hasSubtypes = true
        maxDamage = 0
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing,
            hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val block = worldIn.getBlockState(pos).block
        val canplace = block.isReplaceable(worldIn, pos)
        val itemstack = player.getHeldItem(hand)
        val blockpos = if (canplace) pos else pos.offset(facing)

        if (!canplace) {
            if (block is PipeBlock) {
                val tile = worldIn.getTileEntity(pos)!! as TileEntityPipe

                val thisPipeType = PipeType.values()[itemstack.metadata]

                if (!tile.hasPipeType(thisPipeType)) {
                    tile.addPipeType(thisPipeType)
                    tile.markForUpdate()
                    itemstack.shrink(1)
                    return EnumActionResult.SUCCESS
                }
            }
        }

        val isPosFree = worldIn.checkNoEntityCollision(TileEntityPipe.node.offset(pos))

        return if (player.canPlayerEdit(blockpos,
                        facing,
                        itemstack) && worldIn.mayPlace(worldIn.getBlockState(blockpos).block,
                        blockpos,
                        false,
                        facing,
                        null as Entity?) && isPosFree) {

            val thisPipeType = PipeType.values()[itemstack.metadata]
            worldIn.setBlockState(blockpos, pipe.defaultState.withProperty(PipeBlock.PIPETYPE, thisPipeType))

            if (player is EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger(player, blockpos, itemstack)
            }

            itemstack.shrink(1)
            EnumActionResult.SUCCESS
        } else {
            EnumActionResult.FAIL
        }
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return super.getUnlocalizedName() + "." + PipeType.values()[stack.metadata].unlocalizedName
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (this.isInCreativeTab(tab)) {
            for (i in 0 until PipeType.values().size) {
                items.add(ItemStack(this, 1, i))
            }
        }
    }
}