package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPartHeatExchanger
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerController
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.heatexchanger.TileEntityHeatExchangerOutput
import net.minecraft.block.Block
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

                return@finishUp this@HeatExchangerMultiBlock.recalculatePhysics(validatorCallback)
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
        val tubes = mutableListOf<CoolantTube>()

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

            var comeFrom = inputPort.pos
            var currentPipe = neighbor.first()
            val tube = CoolantTube(inputPort, currentPipe as TileEntityMultiBlockPartHeatExchanger)

            tubeWalking@ while (true) {
                val nextTube = findNeighbor(currentPipe.pos, comeFrom) {
                    it is TileEntityHeatExchangerOutput ||
                            if (tube.isHot) {
                                it.blockType == heatExchangerHotAgentTube
                            } else {
                                it.blockType == heatExchangerColdAgentTube
                            }
                }

                when {
                    nextTube.size > 1 -> {
                        validatorCallback.setLastError("tube flow diverges")
                        return false
                    }
                    nextTube.isEmpty() -> {
                        validatorCallback.setLastError("tube is missing exit")
                        return false
                    }
                    nextTube.first() is TileEntityHeatExchangerOutput -> {
                        tube.output = nextTube.first() as TileEntityHeatExchangerOutput
                        break@tubeWalking
                    }
                    else -> {
                        comeFrom = currentPipe.pos
                        currentPipe = nextTube.first()

                        tube.addTube(currentPipe as TileEntityMultiBlockPartHeatExchanger)
                    }
                }
            }

            tubes.add(tube)
        }

        // count heat matrix
        val interiorMin = this.minimumCoord.add(1, 1, 1)
        val interiorMax = this.maximumCoord.add(-1, -1, -1)

        var matrixType: Block? = null
        var matrix = 0
        for (x in interiorMin.x..interiorMax.x) {
            for (y in interiorMin.y..interiorMax.y) {
                for (z in interiorMin.z..interiorMax.z) {
                    val block = WORLD.getBlockState(BlockPos(x, y, z))
                    if (block.block == heatExchangerColdAgentTube || block.block == heatExchangerHotAgentTube)
                        continue

                    if (matrixType == null) {
                        matrixType = block.block
                    } else if (matrixType != block.block) {
                        validatorCallback.setLastError("matrix consists of different materials ${matrixType.localizedName} and " +
                                block.block.localizedName)
                        return false
                    }

                    matrix++
                }
            }
        }

        this.tubes = tubes
        this.controllerTileEntity!!.updatePhysics(matrix, matrixType)
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
    class CoolantTube(val input: TileEntityHeatExchangerInput, firstPipe: TileEntityMultiBlockPartHeatExchanger) {

        val isHot: Boolean = when {
            firstPipe.blockType == heatExchangerHotAgentTube -> true
            firstPipe.blockType == heatExchangerColdAgentTube -> false
            else -> throw AssertionError("neither hot nor cold tube")
        }

        lateinit var output: TileEntityHeatExchangerOutput

        private val tubeBlocks = mutableListOf<TileEntityMultiBlockPartHeatExchanger>()

        init {
            addTube(firstPipe)
        }

        fun addTube(tubeEntity: TileEntityMultiBlockPartHeatExchanger) {
            if (tubeEntity.blockType == heatExchangerHotAgentTube && isHot)
                tubeBlocks.add(tubeEntity)
            else if (tubeEntity.blockType == heatExchangerColdAgentTube && !isHot)
                tubeBlocks.add(tubeEntity)
            else
                throw IllegalStateException("wrong tile entity for coolant tube")
        }
    }
}