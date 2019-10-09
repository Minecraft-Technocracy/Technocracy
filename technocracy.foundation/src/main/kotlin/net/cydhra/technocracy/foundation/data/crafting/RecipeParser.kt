package net.cydhra.technocracy.foundation.data.crafting

import com.google.gson.JsonObject
import net.minecraftforge.common.crafting.JsonContext

/**
 * Interface describing a parser for custom recipe types of machines of this mod
 */
interface RecipeParser<T : ISpecialRecipe> {

    /**
     * Parse a recipe of type [T] from given json object
     *
     * @param json the json object as it is defined in the recipe resource asset
     * @param context json parsing context
     */
    fun process(json: JsonObject, context: JsonContext): T
}