package net.cydhra.technocracy.foundation.data.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.data.crafting.RecipeParser
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [MFTFRecipe]
 */
object MFTFRecipeParser : RecipeParser<MFTFRecipe> {

    private const val JSON_KEY_INPUT = "input"
    private const val JSON_KEY_OUTPUT_PRIMARY = "output"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse inputs, output and cost and return an [MFTFRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): MFTFRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameters" }
        check(json.has(JSON_KEY_OUTPUT_PRIMARY)) { "recipe is missing primary output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputs = json.getAsJsonArray(JSON_KEY_INPUT).map { getFluidStack(it.asJsonObject) }
        val outputFluid = getFluidStack(json.getAsJsonObject(JSON_KEY_OUTPUT_PRIMARY))

        return MFTFRecipe(inputs, outputFluid, json.get(JSON_KEY_TICK_COST).asInt)
    }

}