package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.tileentity.components.AbstractComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryController
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryHeater
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryInput
import net.cydhra.technocracy.foundation.tileentity.multiblock.refinery.TileEntityRefineryOutput
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
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
    override fun updateClient() {
    }

    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getRecipesByType(RecipeManager.RecipeType.REFINERY) ?: emptyList())
    }

    var controllerTileEntity: TileEntityRefineryController? = null
    private var inputPort: TileEntityRefineryInput? = null
    private var outputPorts: List<TileEntityRefineryOutput> = emptyList()
    var heater: TileEntityRefineryHeater? = null

    private var effectiveHeight = 0

    private var topOutput: TileEntityRefineryOutput? = null
    private var bottomOutput: TileEntityRefineryOutput? = null

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
                this@RefineryMultiBlock.heater = heaters.first()

                return@finishUp this@RefineryMultiBlock.recalculatePhysics(validatorCallback)
            }
        }
    }

    private fun recalculatePhysics(validatorCallback: IMultiblockValidator): Boolean {
        val interiorMin = minimumCoord.add(1, 1, 1)
        val interiorMax = maximumCoord.add(-1, -1, -1)
        effectiveHeight = interiorMax.y - interiorMin.y

        this.topOutput = null
        this.bottomOutput = null
        val firstPort = this.outputPorts[0]

        if (firstPort.pos.y - interiorMin.y > effectiveHeight / 2.0) {
            topOutput = firstPort
        } else {
            bottomOutput = firstPort
        }

        val secondPort = this.outputPorts[1]
        if (secondPort.pos.y - interiorMin.y > effectiveHeight / 2.0) {
            if (topOutput != null) {
                validatorCallback.setLastError("multiblock.error.two_top_outputs")
                return false
            } else {
                topOutput = secondPort
            }
        } else {
            if (bottomOutput != null) {
                validatorCallback.setLastError("multiblock.error.two_bot_outputs")
                return false
            } else {
                bottomOutput = secondPort
            }
        }

        return true
    }

    override fun updateServer(): Boolean {
        val inputFluid = this.controllerTileEntity!!.inputComponent.fluid.currentFluid

        if (inputFluid != null) {
            val recipe = this.recipes.single { it.conforms(stacks = emptyList(), fluids = listOf(inputFluid)) }

            val oilProduced = this.effectiveHeight
            val energyConsumption = oilProduced * recipe.processingCost

            if (this.controllerTileEntity!!.inputComponent.fluid.currentFluid?.amount ?: 0 >= oilProduced &&
                    this.heater!!.energyStorageComponent.energyStorage.currentEnergy >= energyConsumption &&
                    this.controllerTileEntity!!.topTank.currentFluid?.amount ?: 0 < this.controllerTileEntity!!
                            .topTank.capacity &&
                    this.controllerTileEntity!!.bottomTank.currentFluid?.amount ?: 0 < this.controllerTileEntity!!
                            .bottomTank.capacity) {
                this.controllerTileEntity!!.inputComponent.fluid.drain(this.effectiveHeight, true)
                this.heater!!.energyStorageComponent.energyStorage.consumeEnergy(energyConsumption)

                this.controllerTileEntity!!.topTank
                        .fill(FluidStack(recipe.getFluidOutput()[0].fluid, oilProduced), true)
                this.controllerTileEntity!!.bottomTank
                        .fill(FluidStack(recipe.getFluidOutput()[1].fluid, oilProduced), true)
            }
        }
        return true
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 34
    }

    fun getOutputTank(tileEntityRefineryOutput: TileEntityRefineryOutput): IFluidHandler? {
        return if (tileEntityRefineryOutput == topOutput) {
            controllerTileEntity?.topTank
        } else {
            controllerTileEntity?.bottomTank
        }
    }

    override fun getComponents(): MutableList<Pair<String, AbstractComponent>> {
        val components = mutableListOf<Pair<String, AbstractComponent>>()
        if (heater != null) components.addAll(heater!!.getComponents())
        //components.addAll(inputPort!!.getComponents())
        //outputPorts.forEach { components.addAll(it.getComponents()) }
        if (controllerTileEntity != null) components.addAll(controllerTileEntity!!.getComponents())
        return components
    }
}