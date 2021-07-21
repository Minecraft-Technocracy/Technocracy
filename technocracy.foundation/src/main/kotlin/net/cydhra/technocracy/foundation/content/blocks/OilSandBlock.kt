package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.content.blocks.color.ConstantBlockColor
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityFallingBlock
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

class OilSandBlock : AbstractFallingBaseBlock("oil_sand", colorMultiplier = ConstantBlockColor(0x1C1004),
        material = Material.SAND) {

    init {
        this.soundType = SoundType.SAND
        this.blockHardness = 0.5f
        this.blockResistance = 0.5f
    }

    override fun isToolEffective(type: String, state: IBlockState): Boolean {
        return type == "shovel"
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        drops.add(ItemStack(Blocks.SAND))
    }

    override fun getSilkTouchDrop(state: IBlockState): ItemStack {
        return ItemStack(Blocks.SAND)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Blocks.SAND.getItemDropped(state, rand, fortune)
    }

    override fun onStartFalling(fallingEntity: EntityFallingBlock) {
        fallingEntity.shouldDropItem = false
        super.onStartFalling(fallingEntity)
    }
}