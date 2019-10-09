package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [FluidToItemRecipe]
 */
object FTIRecipeParser : RecipeParser<FluidToItemRecipe> {

    private const val JSON_KEY_INPUT = "input"
    private const val JSON_KEY_OUTPUT = "output"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse input, output and cost and return an [FluidToItemRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): FluidToItemRecipe {
        check(json.has(JSON_KEY_INPUT)) { "recipe is missing input parameter" }
        check(json.has(JSON_KEY_OUTPUT)) { "recipe is missing output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputFluid = getFluidStack(json.getAsJsonObject(JSON_KEY_INPUT))
        val outputStack = CraftingHelper.getItemStack(json.getAsJsonObject(JSON_KEY_OUTPUT), context)

        return FluidToItemRecipe(inputFluid, outputStack, json.get(JSON_KEY_TICK_COST).asInt)
    }

}