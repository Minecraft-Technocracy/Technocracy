package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.components.AbstractComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPartHeatExchanger
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
    var tubes: List<CoolantTube> = emptyList()

    override fun updateServer(): Boolean {
        controllerTileEntity?.doWork()
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
                validatorCallback.setLastError("multiblock.error.unused_input_port")
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
                        validatorCallback.setLastError("multiblock.error.tube_flow_diverges")
                        return false
                    }
                    nextTube.isEmpty() -> {
                        validatorCallback.setLastError("multiblock.error.tube_missing_exit")
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

        this.tubes = tubes
        return true
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 1
    }

    /**
     * A helper structure to organize and manage single tubes inside the heat exchanger with their own fluid storages
     * and flows
     */
    inner class CoolantTube(val input: TileEntityHeatExchangerInput, firstPipe: TileEntityMultiBlockPartHeatExchanger) {

        val isHot: Boolean = when {
            firstPipe.blockType == heatExchangerHotAgentTube -> true
            firstPipe.blockType == heatExchangerColdAgentTube -> false
            else -> throw AssertionError("neither hot nor cold tube")
        }

        var output: TileEntityHeatExchangerOutput? = null
            set(value) {
                field = value!!
                this.parts.forEach { it.tubeOutput = value }
            }

        private val tubeBlocks = mutableListOf<TubePart>()

        val parts: List<TubePart> = tubeBlocks

        init {
            addTube(firstPipe)
        }

        fun addTube(tubeEntity: TileEntityMultiBlockPartHeatExchanger) {
            if (tubeEntity.blockType == heatExchangerHotAgentTube && isHot)
                tubeBlocks.add(TubePart(tubeEntity, input))
            else if (tubeEntity.blockType == heatExchangerColdAgentTube && !isHot)
                tubeBlocks.add(TubePart(tubeEntity, input))
            else
                throw IllegalStateException("wrong tile entity for coolant tube")
        }
    }

    /**
     * A wrapper class for tile entities that make up the coolant tubes. The wrapper offers quick access to
     * neighbored tubes that are required for the processing logic.
     *
     * @param part the tile entity of the tube represented by this part
     * @param tubeInput the start of the tube
     */
    inner class TubePart(val part: TileEntityMultiBlockPartHeatExchanger, val tubeInput: TileEntityHeatExchangerInput) {

        private var neighborCache: Set<TubePart>? = null

        private var matrixCount = -1

        lateinit var tubeOutput: TileEntityHeatExchangerOutput

        /**
         *
         */
        fun getMatrixFaceCount(): Int {
            if (matrixCount < 0) {
                val neighborPositions = with(this.part.pos) {
                    arrayOf(up(), down(), north(), east(), south(), west())
                }

                matrixCount = neighborPositions
                        .filter { WORLD.getTileEntity(it) == null }
                        .count()
            }

            return matrixCount
        }

        /**
         * @return a set of [TubePart]s that will take heat from this heat pipe (the method is not intended for cold
         * pipes)
         */
        fun getNeighborCoolingPipes(): Set<TubePart> {
            if (neighborCache == null) {
                val neighborPositions = with(this.part.pos) {
                    arrayOf(up(), down(), north(), east(), south(), west())
                }

                this.neighborCache = this@HeatExchangerMultiBlock.tubes
                        .asSequence()
                        .filter { !it.isHot }
                        .map { it.parts }
                        .flatten()
                        .filter { it.part.pos in neighborPositions }
                        .toSet()
            }

            return neighborCache!!
        }
    }

    override fun getComponents(): MutableList<Pair<String, AbstractComponent>> {
        val components = mutableListOf<Pair<String, AbstractComponent>>()
        inputPorts.forEach { components.addAll(it.getComponents()) }
        outputPorts.forEach { components.addAll(it.getComponents()) }
        if (controllerTileEntity != null) components.addAll(controllerTileEntity!!.getComponents())
        return components
    }

}