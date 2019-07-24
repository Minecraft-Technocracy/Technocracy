package net.cydhra.technocracy.foundation.util.facade.extras.workbench

import net.cydhra.technocracy.foundation.items.FacadeItem
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.block.Block
import net.minecraft.block.BlockWorkbench
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.ContainerWorkbench
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class ContainerFacadeWorkbench(playerInventory: InventoryPlayer, val world: World, val pos: BlockPos) : ContainerWorkbench(playerInventory, world, pos) {
    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        val tile = world.getTileEntity(pos) as? TileEntityPipe ?: return false
        for (facing in EnumFacing.values()) {
            val stack = tile.getFacades()[facing] ?: continue
            if (stack.isEmpty) continue
            val facade = stack.item as FacadeItem
            val facadeStack = facade.getFacadeFromStack(stack)
            val block = Block.getBlockFromItem(facadeStack.stack.item)
            if (block is BlockWorkbench)
                return playerIn.getDistanceSq(this.pos.x.toDouble() + 0.5, this.pos.y.toDouble() + 0.5, this.pos.z.toDouble() + 0.5) <= 64.0
        }
        return false
    }
}