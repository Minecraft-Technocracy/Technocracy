package net.cydhra.technocracy.foundation.util

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.WeightedRandom
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound


class WeightedBlock(val block: Block, var metadata: Int = 0, var data: NBTTagCompound? = null, weight: Int = 100) : WeightedRandom.Item(weight) {

    constructor(ore: ItemStack, weight: Int  = 0) : this(Block.getBlockFromItem(ore.item), ore.itemDamage, null, weight)

    fun isBlockContained(block: Block, metadata: Int, list: Collection<WeightedBlock>): Boolean {
        for (rb in list) {
            if (block.equals(rb.block) && (metadata == -1 || rb.metadata == -1 || rb.metadata == metadata)) {
                return true
            }
        }
        return false
    }

    fun isBlockContained(block: Block, metadata: Int, list: Array<WeightedBlock>): Boolean {
        for (rb in list) {
            if (block.equals(rb.block) && (metadata == -1 || rb.metadata == -1 || rb.metadata == metadata)) {
                return true
            }
        }
        return false
    }

    fun getState() : IBlockState{
        return block.getStateFromMeta(metadata)
    }

}