package net.cydhra.technocracy.foundation.items.general

import net.cydhra.technocracy.foundation.blocks.util.IDynamicBlockDisplayName
import net.cydhra.technocracy.foundation.blocks.util.IDynamicBlockItemProperty
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList


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
}