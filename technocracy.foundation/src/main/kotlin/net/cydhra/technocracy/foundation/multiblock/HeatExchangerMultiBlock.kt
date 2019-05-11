package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerOutput
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Predicate

class HeatExchangerMultiBlock(world: World) :
        BaseMultiBlock(frameBlockWhitelist = Predicate { it.block == heatExchangerWallBlock },
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
                world = world) {

    /**
     * The controller block of the heat exchanger
     */
    private var controllerTileEntity: TileEntityHeatExchangerController? = null

    /**
     * All input ports of the structure walls
     */
    private var inputPorts: List<TileEntityHeatExchangerInput> = emptyList()

    /**
     * All output ports of the structure walls
     */
    private var outputPorts: List<TileEntityHeatExchangerOutput> = emptyList()

    /**
     * A list of all closed tubes inside the machine
     */
    private var tubes: List<CoolantTube> = emptyList()

    override fun updateServer(): Boolean {

        return true
    }

    override fun updateClient() {

    }

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        if (!super.isMachineWhole(validatorCallback)) return false

        return assemble(validatorCallback) {
            val controllers = mutableListOf<TileEntityHeatExchangerController>()
            val inputPorts = mutableListOf<TileEntityHeatExchangerInput>()
            val outputPorts = mutableListOf<TileEntityHeatExchangerOutput>()

            collect(heatExchangerControllerBlock.unlocalizedName, controllers, 1)
            collect(inputPorts)
            collect(outputPorts)

            finishUp {
                this@HeatExchangerMultiBlock.controllerTileEntity = controllers.first()
                this@HeatExchangerMultiBlock.inputPorts = inputPorts
                this@HeatExchangerMultiBlock.outputPorts = outputPorts

                this@HeatExchangerMultiBlock.recalculatePhysics(validatorCallback)

                return@finishUp true
            }
        }
    }

    /**
     * Recalculate all internal physics and connected pipe structures.
     *
     * @param validatorCallback callback for errors in the internal structure
     *
     * @return false if the internal structure is invalid, true if it is valid
     */
    private fun recalculatePhysics(validatorCallback: IMultiblockValidator): Boolean {
        val visited = mutableSetOf<BlockPos>()
        val tubeNetworks = HashMap<TileEntityHeatExchangerInput, CoolantTube>()

        val findNeighbor: (BlockPos, BlockPos?, (TileEntity) -> Boolean) -> Collection<TileEntity> =
                { current, comeFrom, filter ->
                    arrayOf(current.up(), current.down(), current.north(),
                            current.east(), current.south(), current.west())
                            .filter { it != comeFrom }
                            .mapNotNull { WORLD.getTileEntity(it) }
                            .filter(filter)
                }

        this.inputPorts.forEach { inputPort ->
            val neighbor = findNeighbor(inputPort.worldPosition,
                    null) { it.blockType == heatExchangerHotAgentTube || it.blockType == heatExchangerColdAgentTube }

            assert(neighbor.size <= 1)
            if (neighbor.isEmpty()) {
                validatorCallback.setLastError("unused input port")
                return false
            }
        }

        return true
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 1
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

    /**
     * A helper structure to organize and manage single tubes inside the heat exchanger with their own fluid storages
     * and flows
     */
    class CoolantTube {

    }
}