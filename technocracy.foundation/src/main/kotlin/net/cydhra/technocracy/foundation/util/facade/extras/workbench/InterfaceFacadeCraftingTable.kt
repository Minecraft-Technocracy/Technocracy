package net.cydhra.technocracy.foundation.util.facade.extras.workbench

import net.minecraft.block.BlockWorkbench
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class InterfaceFacadeCraftingTable(val world: World, val pos: BlockPos) : BlockWorkbench.InterfaceCraftingTable(world, pos) {
    override fun createContainer(playerInventory: InventoryPlayer, playerIn: EntityPlayer): Container {
        return ContainerFacadeWorkbench(playerInventory, this.world, this.pos)
    }
}