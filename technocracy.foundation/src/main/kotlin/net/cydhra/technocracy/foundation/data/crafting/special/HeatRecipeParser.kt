package net.cydhra.technocracy.foundation.data.crafting.special

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.data.crafting.RecipeParser
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fluids.FluidRegistry

/**
 * Parse the special recipe type [HeatRecipe]. Throws exceptions if the fluid of the recipe does not exist
 */
object HeatRecipeParser : RecipeParser<HeatRecipe> {

    private const val NBT_KEY_COLD_FLUID = "cold"
    private const val NBT_KEY_HOT_FLUID = "hot"
    private const val NBT_KEY_TEMPERATURE_DIFFERENCE = "temperature"
    private const val NBT_KEY_HEAT_PER_DEGREE = "heatCapacity"

    override fun process(json: JsonObject, context: JsonContext): HeatRecipe {
        check(json.has(NBT_KEY_COLD_FLUID)) { "recipe does not have cold fluid type" }
        check(json.has(NBT_KEY_HOT_FLUID)) { "recipe does not have hot fluid type" }
        check(json.has(NBT_KEY_TEMPERATURE_DIFFERENCE)) { "recipe does not have temperature difference" }
        check(json.has(NBT_KEY_HEAT_PER_DEGREE)) { "recipe does not have heat capacity" }

        val coldFluid = FluidRegistry.getFluid(json.get(NBT_KEY_COLD_FLUID).asString)
                ?: throw IllegalStateException("cold fluid does not exist")

        val hotFluid = FluidRegistry.getFluid(json.get(NBT_KEY_HOT_FLUID).asString)
                ?: throw IllegalStateException("hot fluid does not exist")

        val temperatureDelta = json.get(NBT_KEY_TEMPERATURE_DIFFERENCE).asInt
        val heatCapacity = json.get(NBT_KEY_HEAT_PER_DEGREE).asInt

        return HeatRecipe(coldFluid, hotFluid, temperatureDelta, heatCapacity)
    }

}