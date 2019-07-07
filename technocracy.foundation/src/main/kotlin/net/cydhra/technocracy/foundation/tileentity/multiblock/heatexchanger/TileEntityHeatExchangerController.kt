package net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger

import net.cydhra.technocracy.foundation.multiblock.HeatExchangerMultiBlock
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World

class TileEntityHeatExchangerController : TileEntityMultiBlockPart<HeatExchangerMultiBlock>(HeatExchangerMultiBlock::class,
        ::HeatExchangerMultiBlock) {

    override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {

    }

    fun doWork() {
        multiblockController!!.tubes
                .filter(HeatExchangerMultiBlock.CoolantTube::isHot)
                .forEach { tube ->
                    tube.parts.forEach { part ->
                        part.getNeighborCoolingPipes().forEach { coolingNeighbor ->
                            if (coolingNeighbor.tubeInput.fluidComponent.fluid.currentFluid?.amount ?: 0 > 0) {
                                if (part.tubeInput.fluidComponent.fluid.currentFluid?.amount ?: 0 > 0) {
                                    // convert liquid
                                }
                            }
                        }
                    }
                }
    }
}