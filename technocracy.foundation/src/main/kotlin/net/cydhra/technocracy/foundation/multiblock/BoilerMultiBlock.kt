package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.boiler.TileEntityBoilerHeater
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.function.Predicate

class BoilerMultiBlock(world: World) : BaseMultiBlock(
        frameBlockWhitelist = Predicate {
            it.block == boilerWallBlock || it.block == boilerControllerBlock
        },
        sideBlockWhitelist = Predicate {
            it.block == boilerWallBlock || it.block == boilerGlassBlock || it.block == boilerControllerBlock ||
                    it.block == boilerFluidInputBlock
        },
        topBlockWhitelist = Predicate {
            it.block == boilerWallBlock || it.block == boilerGlassBlock ||
                    it.block == boilerFluidInputBlock
        },
        bottomBlockWhitelist = Predicate {
            it.block == boilerWallBlock || it.block == boilerGlassBlock || it.block == boilerHeaterBlock ||
                    it.block == boilerFluidInputBlock
        },
        interiorBlockWhitelist = Predicate {
            it.block == Blocks.AIR
        },
        maximumSizeXZ = 16,
        maximumSizeY = 16,
        world = world) {

    /**
     * The controller tile entity of this multi block structure. Null until the block is found by [isMachineWhole]
     */
    private var controllerTileEntity: TileEntityBoilerController? = null

    /**
     * The heaters of this structure. Empty until the machine is assembled successfully
     */
    private var heaterElements: List<TileEntityBoilerHeater> = emptyList()

    override fun updateServer(): Boolean {

        return true
    }

    override fun updateClient() {

    }

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        if (!super.isMachineWhole(validatorCallback))
            return false

        return assemble(validatorCallback) {
            val controllerTileEntities = mutableListOf<TileEntityBoilerController>()
            val heaterTileEntities = mutableListOf<TileEntityBoilerHeater>()

            collect(boilerControllerBlock.unlocalizedName, controllerTileEntities, 1)
            collect(heaterTileEntities)

            onSuccess {
                this@BoilerMultiBlock.controllerTileEntity = controllerTileEntities.single()
                this@BoilerMultiBlock.heaterElements = heaterTileEntities
            }
        }
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