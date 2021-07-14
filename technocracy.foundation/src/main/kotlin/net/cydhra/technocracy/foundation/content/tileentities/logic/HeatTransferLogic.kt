package net.cydhra.technocracy.foundation.content.tileentities.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicParameters
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityHeatStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.ConversionDirection.COLD_TO_HOT
import net.cydhra.technocracy.foundation.content.tileentities.logic.ConversionDirection.HOT_TO_COLD
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.data.crafting.special.HeatRecipe
import net.minecraftforge.fluids.FluidStack

/**
 * A logic that is used for heat exchange between hot and cold fluids. If heat transfer is not possible, the
 * machine's processing is blocked, so it cannot be used for coolant logic.
 *
 * @param processFluidPerTick how much fluid is converted per tick at maximum. Default is 10 mB, which corresponds to
 * 200 mb fluid per second.
 */
class HeatTransferLogic(
        var processFluidPerTick: Int = 10,
        private val hotFluidComponent: TileEntityFluidComponent,
        private val coldFluidComponent: TileEntityFluidComponent,
        private val direction: ConversionDirection,
        private val heatBuffer: TileEntityHeatStorageComponent
) : ILogic<ILogicParameters> {

    private val inputFluidComponent: TileEntityFluidComponent = if (this.direction == COLD_TO_HOT) coldFluidComponent else hotFluidComponent

    private val outputFluidComponent: TileEntityFluidComponent = if (this.direction == COLD_TO_HOT) hotFluidComponent else coldFluidComponent

    private val recipes by lazy {
        RecipeManager.getSpecialRecipesByType(RecipeManager.RecipeType.HEAT)!!.filterIsInstance<HeatRecipe>()
    }

    private var currentRecipe: HeatRecipe? = null

    override fun preProcessing(logicParameters: ILogicParameters): Boolean {
        // if output is full
        if (this.outputFluidComponent.fluid.capacity == this.outputFluidComponent.fluid.currentFluid?.amount ?: 0) {
            return false
        }

        // check if there is currently a fluid that can be worked
        if (this.inputFluidComponent.fluid.currentFluid != null) {
            this.currentRecipe = this.recipes.single {
                it.coldFluid == this.inputFluidComponent.fluid.currentFluid!!.fluid
                        || it.hotFluid == this.inputFluidComponent.fluid.currentFluid!!.fluid
            }
        } else {
            this.currentRecipe = null
            return false
        }

        // check if output is not blocked by other fluid than current output
        when (this.direction) {
            COLD_TO_HOT -> {
                if (this.hotFluidComponent.fluid.currentFluid != null && this.currentRecipe!!.hotFluid != this
                                .hotFluidComponent.fluid.currentFluid!!.fluid) {
                    return false
                }
            }
            HOT_TO_COLD -> {
                if (this.coldFluidComponent.fluid.currentFluid != null && this.currentRecipe!!.coldFluid != this
                                .coldFluidComponent.fluid.currentFluid!!.fluid) {
                    return false
                }
            }
        }

        // check if heat buffer has required amount of heat/free capacity
        when (this.direction) {
            COLD_TO_HOT -> if (this.heatBuffer.heat < this.currentRecipe!!.milliHeatPerDegree) {
                return false
            }
            HOT_TO_COLD -> if (this.heatBuffer.heatCapacity - this.heatBuffer.heat < this.currentRecipe!!
                            .milliHeatPerDegree) {
                return false
            }
        }

        return true
    }

    override fun processing(logicParameters: ILogicParameters) {
        // process no more heat than input and output can deliver
        var maximumConversionMb = this.processFluidPerTick
                .coerceAtMost(this.outputFluidComponent.fluid.capacity
                        - (this.outputFluidComponent.fluid.currentFluid?.amount ?: 0))
                .coerceAtMost(this.inputFluidComponent.fluid.currentFluid!!.amount)

        // process no more heat than possible within the limits of the heat buffer
        maximumConversionMb = if (this.direction == COLD_TO_HOT) {
            maximumConversionMb.coerceAtMost(this.heatBuffer.heat / (this.currentRecipe!!.milliHeatPerDegree *
                    (currentRecipe!!.hotFluid.temperature - currentRecipe!!.coldFluid.temperature)))
        } else {
            maximumConversionMb.coerceAtMost(
                    (this.heatBuffer.heatCapacity - this.heatBuffer.heat) / (this.currentRecipe!!.milliHeatPerDegree *
                            (currentRecipe!!.hotFluid.temperature - currentRecipe!!.coldFluid.temperature)))
        }

        this.inputFluidComponent.fluid.drain(maximumConversionMb, true, forced = true)
        if (this.direction == COLD_TO_HOT) {
            this.outputFluidComponent.fluid.fill(FluidStack(currentRecipe!!.hotFluid, maximumConversionMb), true)
            this.heatBuffer.drainHeat(maximumConversionMb * this.currentRecipe!!.milliHeatPerDegree *
                    (currentRecipe!!.hotFluid.temperature - currentRecipe!!.coldFluid.temperature))
        } else {
            this.outputFluidComponent.fluid.fill(FluidStack(currentRecipe!!.coldFluid, maximumConversionMb), true)
            this.heatBuffer.fillHeat(maximumConversionMb * this.currentRecipe!!.milliHeatPerDegree *
                    (currentRecipe!!.hotFluid.temperature - currentRecipe!!.coldFluid.temperature))
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ILogicParameters) {

    }
}

/**
 * In which direction the heat transfer should happen
 */
enum class ConversionDirection {
    HOT_TO_COLD, COLD_TO_HOT
}