package net.cydhra.technocracy.foundation.content.multiblock

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicClient
import net.cydhra.technocracy.foundation.api.ecs.logic.LogicClientDelegate
import net.cydhra.technocracy.foundation.content.blocks.*
import net.cydhra.technocracy.foundation.content.tileentities.logic.ConversionDirection
import net.cydhra.technocracy.foundation.content.tileentities.logic.HeatTransferLogic
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.saline.*
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.data.crafting.special.SalineRecipe
import net.cydhra.technocracy.foundation.model.multiblock.api.TiledBaseMultiBlock
import net.minecraft.block.BlockHorizontal
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import java.util.function.Predicate
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor

class SalineMultiBlock(world: World) : ILogicClient by LogicClientDelegate(), TiledBaseMultiBlock(
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
        private set

    private var useHeat = false

    //Average sky light level
    var skyLightLevel = 0
        private set

    //Index of the tile which should be used in the next sky light calculation
    private var skyLightCalculationIndex = 0

    //Sky light levels of all tiles
    private var tileSkyLightLevels: IntArray? = null

    private val recipes: Collection<SalineRecipe> by lazy {
        (RecipeManager.getSpecialRecipesByType(RecipeManager.RecipeType.SALINE)
                ?: emptyList()).filterIsInstance<SalineRecipe>()
    }

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        this.removeLogicStrategy("heat")
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
                if (!it.adjacentTileSides.contains(EnumFacing.WEST))
                    edgeCount++
                //Bottom edge
                if (!it.adjacentTileSides.contains(EnumFacing.SOUTH))
                    edgeCount++
            }

            collect(salineControllerBlock.unlocalizedName, controllers, 1)
            collect(salineFluidInputBlock.unlocalizedName, fluidInputs, edgeCount)
            collect(salineFluidOutputBlock.unlocalizedName, fluidOutputs, tiles.size)
            collect(salineHeatingAgentInputBlock.unlocalizedName, heatingAgentInputs, 0 until edgeCount)
            collect(salineHeatingAgentOutputBlock.unlocalizedName, heatingAgentOutputs, 0 until edgeCount)

            finishUp {
                this@SalineMultiBlock.controllerTileEntity = controllers.first()
                this@SalineMultiBlock.controllerTileEntity!!.heatComponent.heatCapacity =
                        tiles.size * MultiBlockPhysics.salineHeatCapacityPerTile
                this@SalineMultiBlock.tileSkyLightLevels = IntArray(tiles.size)
                skyLightCalculationIndex = 0

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
        if (heatingAgentInputs.isEmpty() && heatingAgentOutputs.isNotEmpty() ||
                heatingAgentInputs.isNotEmpty() && heatingAgentOutputs.isEmpty()) {
            validatorCallback.setLastError("multiblock.saline.error.insufficient_heating_ports")
            return false
        }

        //Validate heating agent positions (only works for size 5x5!)
        val heatingAgents: MutableList<TileEntity> = mutableListOf()
        heatingAgents.addAll(heatingAgentInputs)
        heatingAgents.addAll(heatingAgentOutputs)
        outer@ for (it in heatingAgents) {
            //Input is in the bottom layer of the machine
            if (it.pos.y != minimumCoord.y) {
                validatorCallback.setLastError("multiblock.saline.error.invalid_heating_agent_position", it.pos.x,
                        it.pos.y, it.pos.z)
                return false
            }

            //Each possible x position
            for (x in (minimumCoord.x + 2) until (maximumCoord.x - 1) step 4) {
                if (x == it.pos.x)
                    continue@outer
            }
            //Each possible z position
            for (z in (minimumCoord.z + 2) until (maximumCoord.z - 1) step 4) {
                if (z == it.pos.z)
                    continue@outer
            }

            //Not in the middle of any tile
            validatorCallback.setLastError("multiblock.saline.error.invalid_heating_agent_position", it.pos.x,
                    it.pos.y, it.pos.z)
            return false
        }

        tiles.forEach {
            var tileSkyLightLevel = 0
            //Loop through 3x3 center of tile
            for (x in (it.minPos.x + 1) until it.maxPos.x) {
                for (z in (it.minPos.z + 1) until it.maxPos.z) {
                    //Calculate skylight for each tile
                    tileSkyLightLevel += this.WORLD.getLightFromNeighborsFor(EnumSkyBlock.SKY,
                            BlockPos(x, it.minPos.y, z))

                    //Check for heated floor blocks
                    if (!(x == it.minPos.x + 2 && z == it.minPos.z + 2) && //Ignore center
                            heatingAgents.isNotEmpty() && //Make sure heating is enabled
                            WORLD.getBlockState(BlockPos(x, it.minPos.y, z)).block != salineHeatedWallBlock) {
                        validatorCallback.setLastError("multiblock.saline.error.missing_heated_wall", x,
                                it.minPos.y, z)
                        return false
                    }
                }
            }
            tileSkyLightLevels!![skyLightCalculationIndex] = tileSkyLightLevel / 9
            skyLightCalculationIndex++
        }
        skyLightCalculationIndex = 0
        skyLightLevel = tileSkyLightLevels!!.sum() / tiles.size

        //Bad try to shorten the code
        fun invalidFluidInput(tile: TileEntitySalineFluidInput): Boolean {
            validatorCallback.setLastError("multiblock.saline.error.invalid_fluid_input_position", tile.pos.x,
                    tile.pos.y, tile.pos.z)
            return false
        }

        //Validate fluid input positions (only works for size 5x5!)
        outer@ for (it in fluidInputs) {
            //Input is in the bottom layer of the machine
            if (it.pos.y != maximumCoord.y)
                return invalidFluidInput(it)

            val facing = WORLD.getBlockState(it.pos).getValue(BlockHorizontal.FACING)
            //Each possible x position
            for (x in (minimumCoord.x + 2) until (maximumCoord.x - 1) step 4) {
                if (x == it.pos.x) {
                    if (facing.axis == EnumFacing.Axis.X)
                        return invalidFluidInput(it)
                    continue@outer
                }
            }
            //Each possible z position
            for (z in (minimumCoord.z + 2) until (maximumCoord.z - 1) step 4) {
                if (z == it.pos.z) {
                    if (facing.axis == EnumFacing.Axis.Z)
                        return invalidFluidInput(it)
                    continue@outer
                }
            }

            //Not in the middle of any tile
            return invalidFluidInput(it)
        }

        //Validate outputs
        outer@ for (it in fluidOutputs) {
            for (tile in tiles) {
                if (tile.minPos.add(2, 0, 2) == it.pos)
                    continue@outer
            }
            validatorCallback.setLastError("multiblock.saline.error.invalid_fluid_output_position", it.pos.x,
                    it.pos.y, it.pos.z)
            return false
        }

        //If this is true then everything regarding heat has been validated
        if (heatingAgents.isNotEmpty()) {
            useHeat = true
            this.addLogicStrategy(
                    HeatTransferLogic(
                            processFluidPerTick = MultiBlockPhysics.salineHeatingAgentConversionSpeed * tiles.size,
                            hotFluidComponent = this.controllerTileEntity!!.heatingFluidInputComponent,
                            coldFluidComponent = this.controllerTileEntity!!.heatingFluidOutputComponent,
                            direction = ConversionDirection.HOT_TO_COLD,
                            heatBuffer = this.controllerTileEntity!!.heatComponent), "heat")
        }

        return true
    }

    override fun getComponents(): MutableList<Pair<String, IComponent>> {
        val components = mutableListOf<Pair<String, IComponent>>()
        if (controllerTileEntity != null)
            components.addAll(controllerTileEntity!!.getComponents())
        return components
    }

    private var count = 0
    override fun updateServer(): Boolean {
        //Recalculate sky light for a tile
        if (count % 3 == 0) {
            //TODO: Fix sky light and use it in the formulas below
//            updateSkyLightLevel()
            count = 0
        } else {
            count++
        }

        //Convert heating fluid
        tick()

        //https://github.com/Minecraft-Technocracy/Technocracy/issues/45#issuecomment-600246262

        val inputFluid = this.controllerTileEntity!!.fluidInputComponent.fluid
        val outputFluid = this.controllerTileEntity!!.fluidOutputComponent.fluid
        var totalHeatLoss =
                ceil(this.controllerTileEntity!!.heatComponent.heatCapacity * MultiBlockPhysics.salineHeatLoss)

        if (inputFluid.currentFluid != null) {
            val recipe =
                    this.recipes.single { it.input.unlocalizedName == inputFluid.currentFluid!!.fluid.unlocalizedName }

            val storedHeat = this.controllerTileEntity!!.heatComponent.heat
            val maxHeatUsage =
                    (storedHeat - (storedHeat * exp(-MultiBlockPhysics.salineHeatDrainPerTile))) * tiles.size
            val boostConversion = floor(maxHeatUsage / recipe.heatPerMb).toInt()
            val heatValid = useHeat && this.controllerTileEntity!!.heatComponent.heat - boostConversion > 0F
            //Only add heat boost if heat is enabled and there's enough heat left
            val totalConversion = (MultiBlockPhysics.salineBaseConversionPerTile * tiles.size +
                    (if (heatValid)
                        boostConversion
                    else
                        0)).coerceAtMost(inputFluid.currentFluid!!.amount)

            //Make sure there's enough input and enough space in the output
            if ((outputFluid.currentFluid == null || outputFluid.currentFluid!!.fluid == recipe.output) &&
                    outputFluid.capacity - (outputFluid.currentFluid?.amount ?: 0) > totalConversion) {
                if (heatValid)
                    totalHeatLoss += boostConversion * recipe.heatPerMb

                outputFluid.fill(FluidStack(recipe.output, totalConversion), true)
                inputFluid.drain(totalConversion, true)
            }
        }

        //Loose heat, no matter if anything is processed
        this.controllerTileEntity!!.heatComponent.drainHeat(totalHeatLoss.toInt())

        return true
    }

    private fun updateSkyLightLevel() {
        if (tileSkyLightLevels == null)
            return

        val tile = tiles[skyLightCalculationIndex]
        var tileSkyLightLevel = 0
        for (x in (tile.minPos.x + 1) until tile.maxPos.x) {
            for (z in (tile.minPos.z + 1) until tile.maxPos.z) {
                tileSkyLightLevel += this.WORLD.getLightFor(EnumSkyBlock.SKY,
                        BlockPos(x, tile.minPos.y + 1, z))
            }
        }
        tileSkyLightLevels!![skyLightCalculationIndex] = tileSkyLightLevel / 9

        skyLightLevel = tileSkyLightLevels!!.sum() / tiles.size

        if (skyLightCalculationIndex == tiles.size - 1)
            skyLightCalculationIndex = 0
        else
            skyLightCalculationIndex++
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 41
    }

    override fun updateClient() {
    }
}