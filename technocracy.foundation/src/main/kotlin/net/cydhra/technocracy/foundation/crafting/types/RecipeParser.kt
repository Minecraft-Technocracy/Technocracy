package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.cydhra.technocracy.foundation.crafting.IMachineRecipe
import net.minecraftforge.common.crafting.JsonContext

interface RecipeParser<T : IMachineRecipe> {

    fun process(json: JsonObject, context: JsonContext): T
}