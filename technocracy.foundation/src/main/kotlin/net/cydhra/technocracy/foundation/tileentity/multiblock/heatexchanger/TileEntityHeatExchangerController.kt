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

    companion object {
        private const val NBT_KEY_MATRIX_COEFFICIENT = "matrix_efficiency"
    }

    private var matrixCoefficient = 0.0f

    init {

    }

    fun updatePhysics(matrixSize: Int, matrixType: Block?) {
        this.matrixCoefficient = getMatrixCoefficientForBlock(matrixType)
    }

    private fun getMatrixCoefficientForBlock(block: Block?): Float {
        return 0.5f
    }

    override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {
        player.sendMessage(TextComponentString("Matrix Coefficient: $matrixCoefficient"))
    }

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(data)
        data.setFloat(NBT_KEY_MATRIX_COEFFICIENT, matrixCoefficient)
        return data
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        this.matrixCoefficient = data.getFloat(NBT_KEY_MATRIX_COEFFICIENT)
    }
}