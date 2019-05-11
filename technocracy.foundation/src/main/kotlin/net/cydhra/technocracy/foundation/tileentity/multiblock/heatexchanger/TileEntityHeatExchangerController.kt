package net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger

import net.cydhra.technocracy.foundation.multiblock.HeatExchangerMultiBlock
import net.cydhra.technocracy.foundation.tileentity.AggregatableDelegate
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatable
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.HeatStorageComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class TileEntityHeatExchangerController : TileEntityMultiBlockPart<HeatExchangerMultiBlock>(HeatExchangerMultiBlock::class,
        ::HeatExchangerMultiBlock), TCAggregatableTileEntity, TCAggregatable by AggregatableDelegate() {

    private val heatStorageComponent = HeatStorageComponent(0)

    init {
        this.registerComponent(heatStorageComponent, "heat")
    }

    fun updatePhysics(matrixSize: Int, matrixType: Block?) {
        this.heatStorageComponent.heatCapacity = matrixSize * 100
        this.heatStorageComponent.drainEfficiency = getDrainEfficiencyForBlock(matrixType)
    }

    private fun getDrainEfficiencyForBlock(block: Block?): Float {
        return 0.5f
    }

    override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {
        player.sendMessage(TextComponentString("Heat: ${heatStorageComponent.heat}/${heatStorageComponent.heatCapacity} at " +
                "${heatStorageComponent.drainEfficiency * 100}% (${(heatStorageComponent.heat * heatStorageComponent
                        .drainEfficiency).toInt()})"))
    }
}