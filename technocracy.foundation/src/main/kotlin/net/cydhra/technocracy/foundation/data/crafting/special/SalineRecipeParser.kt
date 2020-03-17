package net.cydhra.technocracy.foundation.data.crafting.special

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.data.crafting.RecipeParser
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fluids.FluidRegistry

/**
 * Parse the special recipe type [SalineRecipe]. Throws exceptions if the fluids of the recipe does not exist
 */
object SalineRecipeParser : RecipeParser<SalineRecipe> {

    private const val NBT_KEY_INPUT_FLUID = "input"
    private const val NBT_KEY_OUTPUT_FLUID = "output"
    private const val NBT_KEY_HEAT_PER_MB = "heatPerMb"

    override fun process(json: JsonObject, context: JsonContext): SalineRecipe {
        check(json.has(NBT_KEY_INPUT_FLUID)) { "recipe does not have an input fluid type" }
        check(json.has(NBT_KEY_OUTPUT_FLUID)) { "recipe does not have an output fluid type" }
        check(json.has(NBT_KEY_HEAT_PER_MB)) { "recipe does not have heat boost modifier" }

        val input = FluidRegistry.getFluid(json.get(NBT_KEY_INPUT_FLUID).asString)
                ?: throw IllegalStateException("input fluid does not exist")

        val output = FluidRegistry.getFluid(json.get(NBT_KEY_OUTPUT_FLUID).asString)
                ?: throw IllegalStateException("output fluid does not exist")

        val heatCapacity = json.get(NBT_KEY_HEAT_PER_MB).asInt

        return SalineRecipe(input, output, heatCapacity)
    }

}