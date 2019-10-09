package net.cydhra.technocracy.foundation.data.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.data.crafting.RecipeParser
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [ITFRecipe]
 */
object ITFRecipeParser : RecipeParser<ITFRecipe> {

    private const val JSON_KEY_INPUT = "input"
    private const val JSON_KEY_OUTPUT = "output"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse input, output and cost and return an [ITFRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): ITFRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameter" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputIngredient = CraftingHelper.getIngredient(json.getAsJsonObject(JSON_KEY_INPUT), context)
        val outputFluid = getFluidStack(json.getAsJsonObject(JSON_KEY_OUTPUT))

        return ITFRecipe(inputIngredient, outputFluid, json.get(JSON_KEY_TICK_COST).asInt)
    }

}