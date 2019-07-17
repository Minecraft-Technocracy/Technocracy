package net.cydhra.technocracy.foundation.items.general

import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList


class ItemSubBlock(block: Block) : ItemBlock(block) {
    override fun getHasSubtypes(): Boolean {
        val list = NonNullList.create<ItemStack>()
        getSubItems(this.creativeTab!!, list)
        return list.size != 1
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        if (block is IDynamicStackDisplayName) {
            return block.getUnlocalizedName(stack)
        }

        return super.getUnlocalizedName(stack)
    }
}