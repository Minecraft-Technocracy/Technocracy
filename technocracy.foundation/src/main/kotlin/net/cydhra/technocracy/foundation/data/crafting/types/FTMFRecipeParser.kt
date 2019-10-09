package net.cydhra.technocracy.foundation.data.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.data.crafting.RecipeParser
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [FTMFRecipe]
 */
object FTMFRecipeParser : RecipeParser<FTMFRecipe> {

    private const val JSON_KEY_INPUT = "input"
    private const val JSON_KEY_OUTPUTS = "outputs"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse input, outputs and cost and return an [FTMFRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): FTMFRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameter" }
        check(json.has(JSON_KEY_OUTPUTS)) { "recipe is missing output parameters" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputFluid = getFluidStack(json.getAsJsonObject(JSON_KEY_INPUT))
        val outputFluids = json.getAsJsonArray(JSON_KEY_OUTPUTS).map { getFluidStack(it.asJsonObject) }

        return FTMFRecipe(inputFluid, outputFluids, json.get(JSON_KEY_TICK_COST).asInt)
    }

}