package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.function.Predicate

class RefineryMultiBlock(world: World) : BaseMultiBlock(
        frameBlockWhitelist = Predicate { it.block == heatExchangerWallBlock },
        sideBlockWhitelist = Predicate {
            it.block == heatExchangerWallBlock || it.block == heatExchangerControllerBlock || it.block == heatExchangerGlassBlock || it.block == heatExchangerInputBlock || it.block == heatExchangerOutputBlock
        },
        topBlockWhitelist = Predicate {
            it.block == heatExchangerWallBlock || it.block == heatExchangerGlassBlock
        },
        bottomBlockWhitelist = Predicate {
            it.block == heatExchangerWallBlock || it.block == heatExchangerGlassBlock
        },
        interiorBlockWhitelist = Predicate { true },
        maximumSizeXZ = 20,
        maximumSizeY = 20,
        world = world
) {


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