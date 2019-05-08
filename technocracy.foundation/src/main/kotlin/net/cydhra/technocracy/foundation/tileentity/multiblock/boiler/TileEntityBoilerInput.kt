package net.cydhra.technocracy.foundation.tileentity.multiblock.boiler

import net.cydhra.technocracy.foundation.multiblock.BoilerMultiBlock
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

/**
 * The tile entity for the input port of a boiler multi-block structure
 */
class TileEntityBoilerInput : TileEntityMultiBlockPart<BoilerMultiBlock>(BoilerMultiBlock::class, ::BoilerMultiBlock) {
    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                && this.multiblockController.fluidHandler != null
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (hasCapability(capability, facing))
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.multiblockController.fluidHandler!!)
        else
            null
    }

    override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {

    }
}
