package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [ItemToItemRecipe]
 */
object ITIRecipeParser : RecipeParser<ItemToItemRecipe> {

    private const val JSON_KEY_INPUT = "input"
    private const val JSON_KEY_OUTPUT_PRIMARY = "output"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse input, output and cost and return an [ItemToItemRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): ItemToItemRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameter" }
        check(json.has(JSON_KEY_OUTPUT_PRIMARY)) { "recipe is missing primary output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputIngredient = CraftingHelper.getIngredient(json.getAsJsonObject(JSON_KEY_INPUT), context)
        val outputStack = CraftingHelper.getItemStack(json.getAsJsonObject(JSON_KEY_OUTPUT_PRIMARY), context)

        return ItemToItemRecipe(inputIngredient, outputStack, json.get(JSON_KEY_TICK_COST).asInt)
    }

}