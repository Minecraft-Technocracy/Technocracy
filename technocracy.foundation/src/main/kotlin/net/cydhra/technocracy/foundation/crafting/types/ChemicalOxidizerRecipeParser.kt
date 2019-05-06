package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * Parser for [CentrifugeRecipe]
 */
object ChemicalOxidizerRecipeParser : RecipeParser<ChemicalOxidizerRecipe> {

    const val JSON_KEY_INPUT = "input"
    const val JSON_KEY_OUTPUT = "output"
    const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse a json object as a pulverizer recipe with input, output and tick cost.
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): ChemicalOxidizerRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameter" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputIngredient = CraftingHelper.getIngredient(json.getAsJsonObject(JSON_KEY_INPUT), context)
        val outputStack = getFluidStack(json.getAsJsonObject(JSON_KEY_OUTPUT), context)

        return ChemicalOxidizerRecipe(inputIngredient, outputStack, json.get(JSON_KEY_TICK_COST).asInt)
    }

}