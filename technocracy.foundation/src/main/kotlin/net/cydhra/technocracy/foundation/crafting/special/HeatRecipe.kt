package net.cydhra.technocracy.foundation.crafting.special

import net.cydhra.technocracy.foundation.crafting.ISpecialRecipe
import net.minecraftforge.fluids.Fluid

/**
 * A recipe for heat exchanging fluids. It is defined by the temperature difference of both fluids and how much heat
 * per degree of temperature this fluid stores. Thus, cooling one bucket of this fluid from hot to cold will result in
 * ``temperatureDifference * heatPerDegree`` heat.
 */
class HeatRecipe(
        val coldFluid: Fluid,
        val hotFluid: Fluid,
        val temperatureDifference: Int,
        val milliHeatPerDegree: Int) : ISpecialRecipe