package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.components.AbstractComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.capacitor.TileEntityCapacitorController
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.function.Predicate

class CapacitorMultiBlock(world: World) : BaseMultiBlock(
        frameBlockWhitelist = Predicate { it.block == capacitorWallBlock },
        sideBlockWhitelist = Predicate {
            it.block == capacitorWallBlock
        },
        topBlockWhitelist = Predicate {
            it.block == capacitorConnectorBlock || it.block == capacitorWallBlock || it.block ==
                    capacitorEnergyPortBlock
        },
        bottomBlockWhitelist = Predicate {
            it.block == capacitorWallBlock
        },
        interiorBlockWhitelist = Predicate {
            it.block == sulfuricAcidBlock || it.block == capacitorElectrodeBlock
        },
        maximumSizeXZ = 20,
        maximumSizeY = 20,
        world = world
) {

    var controllerTileEntity: TileEntityCapacitorController? = null

    override fun getComponents(): MutableList<Pair<String, AbstractComponent>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateServer(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBlockAdded(p0: IMultiblockPart?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateClient() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBlockRemoved(p0: IMultiblockPart?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAssimilate(p0: MultiblockControllerBase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAttachedPartWithMultiblockData(p0: IMultiblockPart?, p1: NBTTagCompound?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMachineAssembled() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun syncDataFrom(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAssimilated(p0: MultiblockControllerBase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMachineRestored() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun syncDataTo(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMachinePaused() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMachineDisassembled() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
