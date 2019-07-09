package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.JsonContext

/**
 * Parser for [ChemicalReactionRecipe]
 */
object RefineryRecipeParser : RecipeParser<RefineryRecipe> {

    const val JSON_KEY_INPUT = "input"
    const val JSON_KEY_OUTPUT = "output"
    const val JSON_KEY_TICK_COST = "cost"

    /**
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): RefineryRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameters" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val input = getFluidStack(json.getAsJsonObject(JSON_KEY_INPUT))

        val outputsArray = json.getAsJsonArray(JSON_KEY_INPUT)
        check(outputsArray.size() == 2) { "recipe requires exactly two outputs" }

        val outputIngredientList = outputsArray.map { element -> getFluidStack(element.asJsonObject) }

        return RefineryRecipe(input, outputIngredientList, json.get(JSON_KEY_TICK_COST).asInt)
    }

}