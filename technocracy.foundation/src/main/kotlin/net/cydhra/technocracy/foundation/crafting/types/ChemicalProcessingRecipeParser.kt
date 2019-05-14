package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * Parser for [ChemicalEtchingRecipe]
 */
object ChemicalProcessingRecipeParser : RecipeParser<ChemicalEtchingRecipe> {

    const val JSON_KEY_INPUT = "input"
    const val JSON_KEY_OUTPUT = "output"
    const val JSON_KEY_TICK_COST = "cost"

    /**
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): ChemicalEtchingRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameters" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputList = json.getAsJsonArray(JSON_KEY_INPUT)
        check(inputList.size() == 3) { "recipe requires exactly three inputs" }

        val inputIngredientList = inputList.map { element -> CraftingHelper.getIngredient(element, context) }
        val outputStack = CraftingHelper.getItemStack(json.getAsJsonObject(JSON_KEY_OUTPUT), context)

        return ChemicalEtchingRecipe(inputIngredientList, outputStack, json.get(JSON_KEY_TICK_COST).asInt)
    }

}