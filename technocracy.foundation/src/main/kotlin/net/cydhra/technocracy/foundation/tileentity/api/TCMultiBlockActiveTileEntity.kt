package net.cydhra.technocracy.foundation.tileentity.api

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface TCMultiBlockActiveTileEntity {

    /**
     * @return true, if the multiblock structure is valid, false if it is incomplete or otherwise invalid.
     */
    fun validateStructure(): Boolean

    fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing)
}