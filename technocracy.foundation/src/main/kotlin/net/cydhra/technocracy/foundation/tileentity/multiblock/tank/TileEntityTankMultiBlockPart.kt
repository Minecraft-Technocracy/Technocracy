package net.cydhra.technocracy.foundation.tileentity.multiblock.tank

import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.multiblock.TankMultiBlock
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.cydhra.technocracy.foundation.tileentity.components.OptionalAttachedComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability


open class TileEntityTankMultiBlockPart : TileEntityMultiBlockPart<TankMultiBlock>(TankMultiBlock::class,
        ::TankMultiBlock), ITileEntityMultiblockController {


    val fluidComp = OptionalAttachedComponent(FluidComponent(DynamicFluidCapability(), EnumFacing.values().toMutableSet()))

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return false
    }

    init {
        fluidComp.innerComponent.fluid.fluidChangeThreshold = 1f

        fluidComp.innerComponent.syncToClient = true
        this.registerComponent(fluidComp, "fluidComponent")
    }
}