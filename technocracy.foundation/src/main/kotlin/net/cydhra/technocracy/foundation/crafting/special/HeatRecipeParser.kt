package net.cydhra.technocracy.foundation.crafting.special

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.crafting.RecipeParser
import net.minecraftforge.common.crafting.JsonContext

object HeatRecipeParser : RecipeParser<HeatRecipe> {
    override fun process(json: JsonObject, context: JsonContext): HeatRecipe {
        TODO("not implemented")
    }

}