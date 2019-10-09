package net.cydhra.technocracy.foundation.crafting

import com.google.gson.JsonObject
import net.minecraftforge.common.crafting.JsonContext

/**
 * Interface describing a parser for custum recipe types of machines of this mod
 */
interface RecipeParser<T : IMachineRecipe> {

    /**
     * Parse a recipe of type [T] from given json object
     *
     * @param json the json object as it is defined in the recipe resource asset
     * @param context json parsing context
     */
    fun process(json: JsonObject, context: JsonContext): T
}