package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.model.blocks.api.AbstractBaseBlock
import net.cydhra.technocracy.foundation.model.blocks.color.ConstantBlockColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*


class OilStoneBlock : AbstractBaseBlock("oil_stone", Material.ROCK, colorMultiplier = ConstantBlockColor(0x1C1004)) {
    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        drops.add(ItemStack(Blocks.STONE))
    }

    override fun getSilkTouchDrop(state: IBlockState): ItemStack {
        return ItemStack(Blocks.STONE)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Blocks.STONE.getItemDropped(state, rand, fortune)
    }
}