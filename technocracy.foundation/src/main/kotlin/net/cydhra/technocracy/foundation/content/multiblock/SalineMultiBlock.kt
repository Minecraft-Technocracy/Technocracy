package net.cydhra.technocracy.foundation.content.multiblock

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.cydhra.technocracy.foundation.content.blocks.*
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.saline.*
import net.cydhra.technocracy.foundation.model.components.IComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.TiledBaseMultiBlock
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import java.util.function.Predicate

class SalineMultiBlock(world: World) : TiledBaseMultiBlock(
        frameBlockWhitelist = Predicate {
            it.block == salineWallBlock || it.block == salineFluidInputBlock || it.block == salineControllerBlock ||
                    it.block == salineHeatingAgentInputBlock || it.block == salineHeatingAgentOutputBlock
        },
        sideBlockWhitelist = null,
        topBlockWhitelist = Predicate {
            it.block == Blocks.AIR
        },
        bottomBlockWhitelist = Predicate {
            it.block == salineWallBlock || it.block == salineHeatedWallBlock || it.block == salineFluidOutputBlock
        },
        interiorBlockWhitelist = null,
        tileFrameBlockWhitelist = Predicate {
            it.block == salineWallBlock || it.block == salineFluidInputBlock
        },
        tileSideBlockWhitelist = Predicate {
            it.block == salineWallBlock || it.block == salineFluidInputBlock || it.block == salineFluidOutputBlock
        },
        tileSizeX = 5,
        tileSizeZ = 5,
        sizeY = 2,
        world = world
) {

    var controllerTileEntity: TileEntitySalineController? = null

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        if (!super.isMachineWhole(validatorCallback)) return false

        return assemble(validatorCallback) {
            val controllers = mutableListOf<TileEntitySalineController>()
            val fluidInputs = mutableListOf<TileEntitySalineFluidInput>()
            val fluidOutputs = mutableListOf<TileEntitySalineFluidOutput>()
            val heatingAgentInputs = mutableListOf<TileEntitySalineHeatingAgentInput>()
            val heatingAgentOutputs = mutableListOf<TileEntitySalineHeatingAgentOutput>()

            var edgeCount = 0
            tiles.forEach {
                //Top and right edge
                edgeCount += 2
                //Left edge
                if(!it.adjacentTileSides.contains(EnumFacing.WEST))
                    edgeCount++
                //Bottom edge
                if(!it.adjacentTileSides.contains(EnumFacing.SOUTH))
                    edgeCount++
            }

            collect(salineControllerBlock.unlocalizedName, controllers, 1)
            collect(salineFluidInputBlock.unlocalizedName, fluidInputs, edgeCount)
            collect(salineFluidOutputBlock.unlocalizedName, fluidOutputs, tiles.size)
            collect(salineHeatingAgentInputBlock.unlocalizedName, heatingAgentInputs, 0 until edgeCount)
            collect(salineHeatingAgentOutputBlock.unlocalizedName, heatingAgentOutputs, 0 until edgeCount)

            finishUp {
                this@SalineMultiBlock.controllerTileEntity = controllers.first()

                return@finishUp this@SalineMultiBlock.recalculatePhysics(validatorCallback, fluidInputs, fluidOutputs,
                        heatingAgentInputs, heatingAgentOutputs)
            }
        }
    }

    private fun recalculatePhysics(validatorCallback: IMultiblockValidator,
                                   fluidInputs: MutableList<TileEntitySalineFluidInput>,
                                   fluidOutputs: MutableList<TileEntitySalineFluidOutput>,
                                   heatingAgentInputs: MutableList<TileEntitySalineHeatingAgentInput>,
                                   heatingAgentOutputs: MutableList<TileEntitySalineHeatingAgentOutput>): Boolean {

        return true
    }

    override fun getComponents(): MutableList<Pair<String, IComponent>> {
        val components = mutableListOf<Pair<String, IComponent>>()
        if (controllerTileEntity != null)
            components.addAll(controllerTileEntity!!.getComponents())
        return components
    }

    override fun updateServer(): Boolean {
        return false
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 41
    }

    override fun updateClient() {
    }
}