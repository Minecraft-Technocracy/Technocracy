package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [ItemToFluidRecipe]
 */
object ITFRecipeParser : RecipeParser<ItemToFluidRecipe> {

    private const val JSON_KEY_INPUT = "input"
    private const val JSON_KEY_OUTPUT = "output"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse input, output and cost and return an [ItemToFluidRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): ItemToFluidRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameter" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputIngredient = CraftingHelper.getIngredient(json.getAsJsonObject(JSON_KEY_INPUT), context)
        val outputFluid = getFluidStack(json.getAsJsonObject(JSON_KEY_OUTPUT))

        return ItemToFluidRecipe(inputIngredient, outputFluid, json.get(JSON_KEY_TICK_COST).asInt)
    }

}