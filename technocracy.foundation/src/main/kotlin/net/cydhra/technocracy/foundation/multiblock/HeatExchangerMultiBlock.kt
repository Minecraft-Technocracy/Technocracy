package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.function.Predicate

class HeatExchangerMultiBlock(world: World) : BaseMultiBlock(
        frameBlockWhitelist = Predicate { true },
        sideBlockWhitelist = Predicate { true },
        topBlockWhitelist = Predicate { true },
        bottomBlockWhitelist = Predicate { true },
        interiorBlockWhitelist = Predicate { true },
        maximumSizeXZ = 20,
        maximumSizeY = 20,
        world = world
) {
    override fun updateServer(): Boolean {

        return true
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {

        return 1
    }

    override fun onBlockAdded(p0: IMultiblockPart?) {

    }

    override fun updateClient() {

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
}