package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.blocks.util.IDynamicBlockDisplayName
import net.cydhra.technocracy.foundation.blocks.util.IDynamicBlockItemProperty
import net.cydhra.technocracy.foundation.blocks.util.IDynamicBlockPlaceBehavior
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class ItemSubBlock(block: Block) : ItemBlock(block) {

    init {
        if (block is IDynamicBlockItemProperty) {
            for (pair in block.getOverrides())
                this.addPropertyOverride(pair.key, pair.value)
        }
    }

    override fun getHasSubtypes(): Boolean {
        val list = NonNullList.create<ItemStack>()
        getSubItems(this.creativeTab!!, list)
        return list.size != 1
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        if (block is IDynamicBlockDisplayName) {
            return block.getUnlocalizedName(stack)
        }

        return super.getUnlocalizedName(stack)
    }

    override fun placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState): Boolean {
        val place = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)

        if (block is IDynamicBlockPlaceBehavior) {
            return block.placeBlockAt(place,stack, player, world, pos, side, hitX, hitY, hitZ, newState)
        }

        return place
    }
}