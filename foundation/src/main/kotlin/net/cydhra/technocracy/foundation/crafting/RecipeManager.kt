package net.cydhra.technocracy.foundation.crafting

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.crafting.RecipeManager.RECIPE_ASSETS_FOLDER
import net.cydhra.technocracy.foundation.crafting.types.PulverizerRecipeParser
import net.cydhra.technocracy.foundation.crafting.types.RecipeParser
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fml.common.Loader
import java.nio.file.Files
import java.nio.file.Path

/**
 * A utility class that loads custom recipe types from [RECIPE_ASSETS_FOLDER] and can be queried for them later.
 */
object RecipeManager {

    /**
     * Assets folder of custom recipe types
     */
    private const val RECIPE_ASSETS_FOLDER = "tcrecipes"

    /**
     * Gson instance for deserializing of the recipes
     */
    private val gson = Gson()

    /**
     * Json context for deserialization
     */
    private val jsonContext = JsonContext(TCFoundation.MODID)

    /**
     * Called upon post initialization by common proxy. Attempts to load recipes of all different machine types.
     */
    fun initialize() {
        parseMachineRecipes("pulverizer", PulverizerRecipeParser)
    }

    /**
     * Load a recipe off a path that has been found by the [CraftingHelper]. The parsed json object is then handed to
     * the given [parser]
     *
     * @param path path where the file is located
     * @param parser the responsible parser for the recipe file
     *
     * @return true if the file has either been loaded successfully or was not loaded on purpose. False in case of an
     * error
     */
    private fun loadRecipe(path: Path, parser: RecipeParser<*>): Boolean {
        if (!path.endsWith(".json"))
            return true

        val jsonObject = try {
            JsonUtils.fromJson(gson, Files.newBufferedReader(path), JsonObject::class.java)!!
        } catch (e: JsonParseException) {
            TCFoundation.logger.error("JSON parse exception", e)
            return false
        }

        try {
            parser.process(jsonObject, jsonContext)
        } catch (e: IllegalStateException) {
            TCFoundation.logger.error("Recipe parse exception", e)
            return false
        }

        return true
    }

    /**
     * Parse all recipes located at a given endpoint that are meant for a given parser
     *
     * @param endpoint subfolder of [RECIPE_ASSETS_FOLDER] where all recipes for the given parser are located
     * @param parser parser for recipes at the given endpoint
     */
    private fun parseMachineRecipes(endpoint: String, parser: RecipeParser<*>) {
        CraftingHelper.findFiles(
                Loader.instance().indexedModList[TCFoundation.MODID],
                "assets/${TCFoundation.MODID}/$RECIPE_ASSETS_FOLDER/$endpoint",
                { true },
                { _, path -> this.loadRecipe(path, parser) },
                true,
                true)
    }
}