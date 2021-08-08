package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.content.blocks.blockWrapper
import net.cydhra.technocracy.foundation.content.blocks.tileWrapper
import net.cydhra.technocracy.foundation.content.tileentities.wrapper.BlockWrapperTileEntity
import net.cydhra.technocracy.foundation.util.structures.BlockInfo
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DebugItem : BaseItem("debugitem") {

    override fun onItemUseFirst(
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        hand: EnumHand
    ): EnumActionResult {
        if (!world.isRemote) {
            if (world.getTileEntity(pos) is BlockWrapperTileEntity) {
                return EnumActionResult.FAIL
            }

            val state = world.getBlockState(pos)
            val block = state.block

            val hasTile = block.hasTileEntity()

            val info = if (hasTile) {
                BlockInfo(
                    BlockPos.ORIGIN,
                    block,
                    block.getMetaFromState(state),
                    world.getTileEntity(pos)!!.serializeNBT(),
                    state
                )
            } else {
                BlockInfo(BlockPos.ORIGIN, block, block.getMetaFromState(state), null, state)
            }

            val newState = if (hasTile) {
                tileWrapper.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, 0, player, hand)
            } else {
                blockWrapper.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, 0, player, hand)
            }

            //remove tile so no items can be droped
            world.removeTileEntity(pos)

            if (!world.setBlockState(pos, newState)) return EnumActionResult.PASS

            val tile = world.getTileEntity(pos) as BlockWrapperTileEntity
            tile.block = info
            tile.markDirty()

            return EnumActionResult.SUCCESS

        }
        return EnumActionResult.PASS
    }

}