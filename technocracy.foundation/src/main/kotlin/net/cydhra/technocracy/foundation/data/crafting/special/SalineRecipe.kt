package net.cydhra.technocracy.foundation.data.crafting.special

import net.cydhra.technocracy.foundation.data.crafting.ISpecialRecipe
import net.minecraftforge.fluids.Fluid

/**
 * A recipe for the saline multiblock. It defines an input and output fluid and a boost threshold which is the amount
 * of heat that is needed to convert one more milli-bucket of input fluid.
 */
class SalineRecipe(
        val input: Fluid,
        val output: Fluid,
        val heatPerMb: Int) : ISpecialRecipe