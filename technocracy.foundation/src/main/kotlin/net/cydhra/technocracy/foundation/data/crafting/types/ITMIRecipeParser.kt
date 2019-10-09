package net.cydhra.technocracy.foundation.data.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.data.crafting.RecipeParser
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [ITMIRecipe]
 */
object ITMIRecipeParser : RecipeParser<ITMIRecipe> {

    private const val JSON_KEY_INPUT = "input"
    private const val JSON_KEY_OUTPUT = "outputs"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse input, outputs and cost and return an [ITMIRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): ITMIRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameter" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameters" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputIngredient = CraftingHelper.getIngredient(json.getAsJsonObject(JSON_KEY_INPUT), context)
        val outputStacks = json.getAsJsonArray(JSON_KEY_OUTPUT)
                .map { CraftingHelper.getItemStack(it.asJsonObject, context) }

        return ITMIRecipe(inputIngredient, outputStacks, json.get(JSON_KEY_TICK_COST).asInt)
    }

}