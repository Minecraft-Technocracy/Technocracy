package net.cydhra.technocracy.foundation.blocks.api

import net.cydhra.technocracy.foundation.blocks.PlainBlock
import net.cydhra.technocracy.foundation.blocks.color.DyeBlockColor
import net.cydhra.technocracy.foundation.blocks.util.IDynamicBlockDisplayName
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class ColoredPlainBlock(unlocalizedName: String,
                        material: Material,
                        opaque: Boolean = true,
                        renderLayer: BlockRenderLayer = BlockRenderLayer.SOLID)
    : PlainBlock(unlocalizedName, material, opaque, renderLayer, DyeBlockColor), IDynamicBlockDisplayName {

    init {
        this.defaultState = this.blockState.baseState.withProperty(DyeBlockColor.COLOR, EnumDyeColor.WHITE)
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        for (color in EnumDyeColor.values()) {
            items.add(ItemStack(this, 1, color.metadata))
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        return (state.getValue<EnumDyeColor>(DyeBlockColor.COLOR) as EnumDyeColor).metadata
    }


    /**
     * Get the MapColor for this Block and the given BlockState
     */
    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        return MapColor.getBlockColor(state.getValue<EnumDyeColor>(DyeBlockColor.COLOR) as EnumDyeColor)
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(DyeBlockColor.COLOR, EnumDyeColor.byMetadata(meta))
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue<EnumDyeColor>(DyeBlockColor.COLOR) as EnumDyeColor).metadata
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, DyeBlockColor.COLOR)
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return "color." + EnumDyeColor.byMetadata(stack.metadata).unlocalizedName + ".name " + unlocalizedName
    }
}