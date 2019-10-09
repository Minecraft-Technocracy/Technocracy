package net.cydhra.technocracy.foundation.data.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.data.crafting.RecipeParser
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [IFTFRecipe]
 */
object IFTFRecipeParser : RecipeParser<IFTFRecipe> {

    private const val JSON_KEY_INPUT_ITEM = "item"
    private const val JSON_KEY_INPUT_FLUID = "fluid"
    private const val JSON_KEY_OUTPUT_PRIMARY = "output"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse inputs, output and cost and return an [IFTFRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): IFTFRecipe {
        check(json.has(JSON_KEY_INPUT_ITEM)) { "recipe is missing item input parameter" }
        check(json.has(JSON_KEY_INPUT_FLUID)) { "recipe is missing fluid input parameter " }
        check(json.has(JSON_KEY_OUTPUT_PRIMARY)) { "recipe is missing primary output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputIngredient = CraftingHelper.getIngredient(json.getAsJsonObject(JSON_KEY_INPUT_ITEM), context)
        val inputFluid = getFluidStack(json.getAsJsonObject(JSON_KEY_INPUT_FLUID))
        val outputFluid = getFluidStack(json.getAsJsonObject(JSON_KEY_OUTPUT_PRIMARY))

        return IFTFRecipe(inputIngredient, inputFluid, outputFluid, json.get(JSON_KEY_TICK_COST).asInt)
    }

}