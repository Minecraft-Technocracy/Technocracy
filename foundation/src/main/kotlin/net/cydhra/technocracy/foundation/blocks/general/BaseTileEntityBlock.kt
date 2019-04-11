package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A base block that provides a tile entity.
 */
abstract class BaseTileEntityBlock(unlocalizedName: String,
                                   registryName: String = unlocalizedName,
                                   colorMultiplier: ConstantBlockColor? = null,
                                   material: Material)
    : AbstractRotateableBlock(unlocalizedName, material, registryName, colorMultiplier), ITileEntityProvider {



    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) {
            //TODO open gui
            //playerIn.openGui()
        }
        return true
    }

}