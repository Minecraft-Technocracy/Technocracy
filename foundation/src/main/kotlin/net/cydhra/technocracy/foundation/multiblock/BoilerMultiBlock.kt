package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.boilerControllerBlock
import net.cydhra.technocracy.foundation.blocks.general.boilerWallBlock
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityBoilerController
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.function.Predicate

class BoilerMultiBlock(world: World) : BaseMultiBlock(
        frameBlockWhitelist = Predicate { it.block == boilerWallBlock || it.block == boilerControllerBlock },
        sideBlockWhitelist = Predicate { it.block == boilerWallBlock || it.block == boilerControllerBlock },
        topBlockWhitelist = Predicate { it.block == boilerWallBlock },
        bottomBlockWhitelist = Predicate { it.block == boilerWallBlock },
        interiorBlockWhitelist = Predicate { it.block == Blocks.AIR },
        maximumSizeXZ = 16,
        maximumSizeY = 16,
        world = world) {

    private var controllerTileEntity: TileEntityBoilerController? = null

    override fun updateServer(): Boolean {
        return true
    }

    override fun updateClient() {

    }

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        if (!super.isMachineWhole(validatorCallback))
            return false

        var controllerTileEntity: TileEntityBoilerController? = null

        for (part in this.connectedParts) {
            if (part is TileEntityBoilerController) {
                if (controllerTileEntity == null)
                    controllerTileEntity = part
                else {
                    validatorCallback.setLastError("only one controller is allowed")
                    return false
                }
            }
        }

        if (controllerTileEntity == null) {
            validatorCallback.setLastError("controller is missing")
            return false
        }

        this.controllerTileEntity = controllerTileEntity
        return true
    }

    override fun onBlockAdded(p0: IMultiblockPart?) {

    }

    override fun onBlockRemoved(p0: IMultiblockPart?) {

    }

    override fun onAssimilate(p0: MultiblockControllerBase?) {

    }

    override fun onAttachedPartWithMultiblockData(p0: IMultiblockPart?, p1: NBTTagCompound?) {

    }

    override fun onMachineAssembled() {

    }

    override fun syncDataFrom(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {

    }

    override fun onAssimilated(p0: MultiblockControllerBase?) {

    }

    override fun onMachineRestored() {

    }

    override fun syncDataTo(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {

    }

    override fun onMachinePaused() {

    }

    override fun onMachineDisassembled() {

    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 26
    }
}