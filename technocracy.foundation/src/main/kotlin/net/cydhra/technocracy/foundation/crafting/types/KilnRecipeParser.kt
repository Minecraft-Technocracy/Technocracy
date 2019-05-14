package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.JsonContext

/**
 * Parser for [KilnRecipe]
 */
object KilnRecipeParser : RecipeParser<KilnRecipe> {

    const val JSON_KEY_INPUT = "input"
    const val JSON_KEY_OUTPUT = "output"
    const val JSON_KEY_TICK_COST = "cost"

    /**
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): KilnRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameters" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputStack = getFluidStack(json.getAsJsonObject(JSON_KEY_INPUT), context)
        val outputStack = getFluidStack(json.getAsJsonObject(JSON_KEY_OUTPUT), context)

        return KilnRecipe(inputStack, outputStack, json.get(JSON_KEY_TICK_COST).asInt)
    }

}