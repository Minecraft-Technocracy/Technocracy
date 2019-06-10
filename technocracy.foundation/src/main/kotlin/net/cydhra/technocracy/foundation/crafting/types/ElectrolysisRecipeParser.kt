package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.JsonContext

/**
 * Parser for [ElectrolysisRecipe]
 */
object ElectrolysisRecipeParser : RecipeParser<ElectrolysisRecipe> {

    const val JSON_KEY_INPUT = "input"
    const val JSON_KEY_OUTPUT = "output"
    const val JSON_KEY_TICK_COST = "cost"

    /**
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): ElectrolysisRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameter" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameters" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val outputList = json.getAsJsonArray(JSON_KEY_OUTPUT)
        check(outputList.size() == 2) { "recipe requires exactly two outputs" }

        val input = getFluidStack(json.get(JSON_KEY_INPUT).asJsonObject)
        val ouputs = outputList.map { element -> getFluidStack(element.asJsonObject) }

        return ElectrolysisRecipe(input, ouputs, json.get(JSON_KEY_TICK_COST).asInt)
    }

}