package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.JsonContext

/**
 * Parser for [ChemicalReactionRecipe]
 */
object ChemicalReactionRecipeParser : RecipeParser<ChemicalReactionRecipe> {

    const val JSON_KEY_INPUT = "input"
    const val JSON_KEY_OUTPUT = "output"
    const val JSON_KEY_TICK_COST = "cost"

    /**
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): ChemicalReactionRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameters" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputList = json.getAsJsonArray(JSON_KEY_INPUT)
        check(inputList.size() == 2) { "recipe requires exactly two inputs" }

        val inputIngredientList = inputList.map { element -> getFluidStack(element.asJsonObject) }
        val outputStack = getFluidStack(json.getAsJsonObject(JSON_KEY_OUTPUT))

        return ChemicalReactionRecipe(inputIngredientList, outputStack, json.get(JSON_KEY_TICK_COST).asInt)
    }

}