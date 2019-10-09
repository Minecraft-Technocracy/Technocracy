package net.cydhra.technocracy.foundation.data.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.data.crafting.RecipeParser
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [MITIRecipe]
 */
object MITIRecipeParser : RecipeParser<MITIRecipe> {

    private const val JSON_KEY_INPUT = "input"
    private const val JSON_KEY_OUTPUT_PRIMARY = "output"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse inputs, output and cost and return an [MITIRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): MITIRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameters" }
        check(json.has(JSON_KEY_OUTPUT_PRIMARY)) { "recipe is missing primary output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputs = json.getAsJsonArray(JSON_KEY_INPUT).map { CraftingHelper.getIngredient(it, context) }
        val outputStack = CraftingHelper.getItemStack(json.getAsJsonObject(JSON_KEY_OUTPUT_PRIMARY), context)

        return MITIRecipe(inputs, outputStack, json.get(JSON_KEY_TICK_COST).asInt)
    }

}