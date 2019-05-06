package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * Parser for [CentrifugeRecipe]
 */
object CentrifugeRecipeParser : RecipeParser<CentrifugeRecipe> {

    const val JSON_KEY_INPUT = "input"
    const val JSON_KEY_OUTPUT_PRIMARY = "output_primary"
    const val JSON_KEY_OUTPUT_SECONDARY = "output_secondary"
    const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse a json object as a pulverizer recipe with input, output and tick cost.
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): CentrifugeRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameter" }
        check(json.has(JSON_KEY_OUTPUT_PRIMARY)) { "recipe is missing primary output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputIngredient = CraftingHelper.getIngredient(json.getAsJsonObject(JSON_KEY_INPUT), context)
        val outputStack = CraftingHelper.getItemStack(json.getAsJsonObject(JSON_KEY_OUTPUT_PRIMARY), context)

        val secondaryStack = if (json.has(JSON_KEY_OUTPUT_SECONDARY)) {
            CraftingHelper.getItemStack(json.getAsJsonObject(JSON_KEY_OUTPUT_SECONDARY), context)
        } else {
            null
        }

        return CentrifugeRecipe(inputIngredient, outputStack, secondaryStack, json.get(JSON_KEY_TICK_COST).asInt)
    }

}