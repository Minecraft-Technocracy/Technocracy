package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryController
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryHeater
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryOutput
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import java.util.function.Predicate

class RefineryMultiBlock(world: World) : BaseMultiBlock(
        frameBlockWhitelist = Predicate { it.block == refineryWallBlock },
        sideBlockWhitelist = Predicate {
            it.block == refineryWallBlock || it.block == refineryControllerBlock || it.block ==
                    refineryInputBlock || it.block == refineryOutputBlock
        },
        topBlockWhitelist = Predicate {
            it.block == refineryWallBlock
        },
        bottomBlockWhitelist = Predicate {
            it.block == refineryHeaterBlock
        },
        interiorBlockWhitelist = Predicate { it.block == Blocks.AIR },
        maximumSizeXZ = 3,
        maximumSizeY = 20,
        world = world
) {

    private var controllerTileEntity: TileEntityRefineryController? = null

    private var inputPort: TileEntityRefineryInput? = null

    private var outputPorts: List<TileEntityRefineryOutput> = emptyList()

    private var heaters: TileEntityRefineryHeater? = null

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        if (!super.isMachineWhole(validatorCallback)) return false

        return assemble(validatorCallback) {
            val controllers = mutableListOf<TileEntityRefineryController>()
            val inputPorts = mutableListOf<TileEntityRefineryInput>()
            val outputPorts = mutableListOf<TileEntityRefineryOutput>()
            val heaters = mutableListOf<TileEntityRefineryHeater>()

            collect(refineryControllerBlock.unlocalizedName, controllers, 1)
            collect(refineryInputBlock.unlocalizedName, inputPorts, 1)
            collect(refineryOutputBlock.unlocalizedName, outputPorts, 2)
            collect(refineryHeaterBlock.unlocalizedName, heaters, 1)

            finishUp {
                this@RefineryMultiBlock.controllerTileEntity = controllers.first()
                this@RefineryMultiBlock.inputPort = inputPorts.first()
                this@RefineryMultiBlock.outputPorts = outputPorts
                this@RefineryMultiBlock.heaters = heaters.first()

                this@RefineryMultiBlock.recalculatePhysics()
                return@finishUp true
            }
        }
    }

    private fun recalculatePhysics() {

    }

    override fun updateServer(): Boolean {
        return true
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 34
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