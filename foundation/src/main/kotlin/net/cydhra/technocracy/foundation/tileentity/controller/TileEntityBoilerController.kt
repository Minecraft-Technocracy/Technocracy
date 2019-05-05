package net.cydhra.technocracy.foundation.tileentity.controller

import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.cydhra.technocracy.foundation.multiblock.BoilerMultiBlock
import net.cydhra.technocracy.foundation.tileentity.AbstractRectangularMultiBlockTileEntity

class TileEntityBoilerController : AbstractRectangularMultiBlockTileEntity() {
    override fun createNewMultiblock(): MultiblockControllerBase {
        return BoilerMultiBlock(this.world)
    }

    override fun getMultiblockControllerType(): Class<out MultiblockControllerBase> {
        return BoilerMultiBlock::class.java
    }

    override fun onMachineActivated() {

    }

    override fun onMachineDeactivated() {

    }

    override fun isGoodForSides(v: IMultiblockValidator) = true
    override fun isGoodForFrame(v: IMultiblockValidator) = true
    override fun isGoodForTop(v: IMultiblockValidator) = true
    override fun isGoodForInterior(v: IMultiblockValidator) = false
    override fun isGoodForBottom(v: IMultiblockValidator) = true

    override fun validateStructure(): Boolean {
        return this.multiblockController.isAssembled
    }
}