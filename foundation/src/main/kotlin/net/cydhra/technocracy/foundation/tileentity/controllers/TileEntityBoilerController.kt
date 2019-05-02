package net.cydhra.technocracy.foundation.tileentity.controllers

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.tileentity.AbstractRectangularMultiBlockControllerTileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.function.Predicate

class TileEntityBoilerController(world: World) : AbstractRectangularMultiBlockControllerTileEntity(
        frameBlockWhitelist = Predicate { true },
        sideBlockWhitelist = Predicate { true },
        topBlockWhitelist = Predicate { true },
        bottomBlockWhitelist = Predicate { true },
        interiorBlockWhitelist = Predicate { true },
        maximumSizeXZ = 16,
        maximumSizeY = 16,
        world = world) {

    override fun updateServer(): Boolean {
        TODO("not implemented")
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        TODO("not implemented")
    }

    override fun onBlockAdded(p0: IMultiblockPart?) {
        TODO("not implemented")
    }

    override fun updateClient() {
        TODO("not implemented")
    }

    override fun onBlockRemoved(p0: IMultiblockPart?) {
        TODO("not implemented")
    }

    override fun onAssimilate(p0: MultiblockControllerBase?) {
        TODO("not implemented")
    }

    override fun onAttachedPartWithMultiblockData(p0: IMultiblockPart?, p1: NBTTagCompound?) {
        TODO("not implemented")
    }

    override fun onMachineAssembled() {
        TODO("not implemented")
    }

    override fun syncDataFrom(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {
        TODO("not implemented")
    }

    override fun onAssimilated(p0: MultiblockControllerBase?) {
        TODO("not implemented")
    }

    override fun onMachineRestored() {
        TODO("not implemented")
    }

    override fun syncDataTo(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {
        TODO("not implemented")
    }

    override fun onMachinePaused() {
        TODO("not implemented")
    }

    override fun onMachineDisassembled() {
        TODO("not implemented")
    }

}