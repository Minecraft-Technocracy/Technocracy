package net.cydhra.technocracy.astronautics.blocks

import net.cydhra.technocracy.astronautics.tileentity.RocketControllerTileEntity
import net.cydhra.technocracy.foundation.blocks.api.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.util.structures.Template
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World


class RocketControllerBlock : AbstractTileEntityBlock("rocket_controller", material = Material.ROCK) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return RocketControllerTileEntity()
    }

    val launchpad = Template()

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!launchpad.init) {
            launchpad.loadFromAssets("launchpad")
        }

        val matches = launchpad.matches(worldIn, pos, true)

        if (matches != null)
            playerIn.sendStatusMessage(TextComponentString("Launchpad built"), true)


        return false
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack(this)
    }
}