package net.cydhra.technocracy.foundation.data.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.data.crafting.RecipeParser
import net.cydhra.technocracy.foundation.util.getFluidStack
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext

/**
 * General parser for [MIMFTIRecipe]
 */
object MIMFTIRecipeParser : RecipeParser<MIMFTIRecipe> {

    private const val JSON_KEY_INPUT_ITEMS = "items"
    private const val JSON_KEY_INPUT_FLUIDS = "fluids"
    private const val JSON_KEY_OUTPUT_PRIMARY = "output"
    private const val JSON_KEY_TICK_COST = "cost"

    /**
     * Parse inputs, output and cost and return an [MFTFRecipe]
     *
     * @throws IllegalStateException if any of the parameters is missing in the json file
     */
    override fun process(json: JsonObject, context: JsonContext): MIMFTIRecipe {
        check(json.has(JSON_KEY_INPUT_ITEMS)) { "recipe is missing input item parameters" }
        check(json.has(JSON_KEY_INPUT_FLUIDS)) { "recipe is missing input fluid parameters" }
        check(json.has(JSON_KEY_OUTPUT_PRIMARY)) { "recipe is missing primary output parameter" }
        check(json.has(JSON_KEY_TICK_COST)) { "recipe is missing cost parameter" }

        val inputFluids = json.getAsJsonArray(JSON_KEY_INPUT_FLUIDS).map { getFluidStack(it.asJsonObject) }
        val inputItems = json.getAsJsonArray(JSON_KEY_INPUT_ITEMS).map { CraftingHelper.getIngredient(it, context) }
        val outputStack = CraftingHelper.getItemStack(json.getAsJsonObject(JSON_KEY_OUTPUT_PRIMARY), context)

        return MIMFTIRecipe(inputItems, inputFluids, outputStack, json.get(JSON_KEY_TICK_COST).asInt)
    }

}