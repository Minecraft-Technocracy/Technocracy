package net.cydhra.technocracy.foundation.content.tileentities.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicParameters
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityHeatStorageComponent
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.data.crafting.special.HeatRecipe
import net.minecraftforge.fluids.FluidStack

/**
 * A logic that processes hot fluids producing cold fluids and heat.
 *
 * @param processFluidPerTick how much fluid is converted per tick at maximum. Default is 10 mB, which corresponds to
 * 200 mb fluid per second.
 */
class CoolingLogic(
    var processFluidPerTick: Int = 10,
    private val hotFluidComponent: TileEntityFluidComponent,
    private val coldFluidComponent: TileEntityFluidComponent,
    private val heatBuffer: TileEntityHeatStorageComponent
) : ILogic<ILogicParameters> {

    private val recipes by lazy {
        RecipeManager.getSpecialRecipesByType(RecipeManager.RecipeType.HEAT)!!.filterIsInstance<HeatRecipe>()
    }

    private var currentRecipe: HeatRecipe? = null

    override fun preProcessing(logicParameters: ILogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ILogicParameters) {

    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ILogicParameters) {
        // if output is full
        if (this.hotFluidComponent.fluid.capacity == this.hotFluidComponent.fluid.currentFluid?.amount ?: 0) {
            return
        }

        // check if there is currently a fluid that can be worked
        if (this.coldFluidComponent.fluid.currentFluid != null) {
            this.currentRecipe = this.recipes.single {
                it.coldFluid == this.coldFluidComponent.fluid.currentFluid!!.fluid
            }
        } else {
            this.currentRecipe = null
            return
        }

        // check if output is not blocked by other fluid than current output
        if (this.hotFluidComponent.fluid.currentFluid != null
            && this.currentRecipe!!.hotFluid != this.hotFluidComponent.fluid.currentFluid!!.fluid
        ) {
            return
        }


        // check if heat buffer has required amount of heat/free capacity
        if (this.heatBuffer.heat < this.currentRecipe!!.milliHeatPerDegree) {
            return
        }

        // process no more heat than input and output can deliver
        var maximumConversionMb = this.processFluidPerTick
            .coerceAtMost(
                this.hotFluidComponent.fluid.capacity
                        - (this.hotFluidComponent.fluid.currentFluid?.amount ?: 0)
            )
            .coerceAtMost(this.coldFluidComponent.fluid.currentFluid!!.amount)

        // process no more heat than possible within the limits of the heat buffer
        var tempDelta = (this.currentRecipe!!.hotFluid.temperature - this.currentRecipe!!.coldFluid.temperature)

        // hotfix for mekanism having the wrong temperature for liquid steam which sadly overrides our steam
        if (tempDelta == 0)
            tempDelta = 80

        maximumConversionMb =
            maximumConversionMb.coerceAtMost(this.heatBuffer.heat / (this.currentRecipe!!.milliHeatPerDegree * tempDelta))


        this.coldFluidComponent.fluid.drain(maximumConversionMb, true, forced = true)
        this.hotFluidComponent.fluid.fill(
            FluidStack(currentRecipe!!.hotFluid, maximumConversionMb),
            doFill = true,
            forced = true
        )
        this.heatBuffer.heat -= maximumConversionMb * this.currentRecipe!!.milliHeatPerDegree *
                tempDelta
    }
}