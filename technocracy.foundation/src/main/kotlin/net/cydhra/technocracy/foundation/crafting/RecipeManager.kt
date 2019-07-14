package net.cydhra.technocracy.foundation.crafting

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.crafting.RecipeManager.RECIPE_ASSETS_FOLDER
import net.cydhra.technocracy.foundation.crafting.types.*
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fml.common.Loader
import org.apache.commons.io.FilenameUtils
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
     * All recipes loaded by type
     */
    private val loadedRecipes: Multimap<RecipeType, IMachineRecipe> = HashMultimap.create()

    /**
     * Called upon post initialization by common proxy. Attempts to load recipes of all different machine types.
     */
    fun initialize() {
        parseMachineRecipes("alloy_smeltery", AlloySmelteryRecipeParser, RecipeManager.RecipeType.ALLOY)
        parseMachineRecipes("centrifuge", CentrifugeRecipeParser, RecipeManager.RecipeType.CENTRIFUGE)
        parseMachineRecipes("chemical_etching", ChemicalEtchingParser, RecipeManager.RecipeType.CHEMICAL_ETCHING)
        parseMachineRecipes("chemical_oxidizer", ChemicalOxidizerRecipeParser, RecipeManager.RecipeType.CHEMICAL_OXIDIZER)
        parseMachineRecipes("chemical_processing", ChemicalProcessingParser, RecipeManager.RecipeType.CHEMICAL_PROCESSING)
        parseMachineRecipes("chemical_reaction", ChemicalReactionRecipeParser, RecipeManager.RecipeType.CHEMICAL_REACTION)
        parseMachineRecipes("compactor", CompactorRecipeParser, RecipeManager.RecipeType.COMPACTOR)
        parseMachineRecipes("electric_furnace", ElectricFurnaceRecipeParser, RecipeManager.RecipeType.ELECTRIC_FURNACE)
        parseMachineRecipes("electrolysis", ElectrolysisRecipeParser, RecipeManager.RecipeType.ELECTROLYSIS)
        parseMachineRecipes("kiln", KilnRecipeParser, RecipeManager.RecipeType.KILN)
        parseMachineRecipes("pulverizer", PulverizerRecipeParser, RecipeManager.RecipeType.PULVERIZER)
        parseMachineRecipes("refinery", RefineryRecipeParser, RecipeManager.RecipeType.REFINERY)
    }

    /**
     * Query all recipes of a specific type
     *
     * @param type request recipe type
     *
     * @return an immutable collection of registered recipes of the specified type
     */
    fun getRecipesByType(type: RecipeType): Collection<IMachineRecipe>? {
        return loadedRecipes[type]
    }


    /**
     * Load a recipe off a path that has been found by the [CraftingHelper]. The parsed json object is then handed to
     * the given [parser]
     *
     * @param path path where the file is located
     * @param parser the responsible parser for the recipe file
     * @param type recipe type that is loaded
     *
     * @return true if the file has either been loaded successfully or was not loaded on purpose. False in case of an
     * error
     */
    private fun loadRecipe(path: Path, parser: RecipeParser<*>, type: RecipeType): Boolean {
        if (FilenameUtils.getExtension(path.toString()) != "json")
            return true

        val jsonObject = try {
            Files.newBufferedReader(path).use { reader ->
                JsonUtils.fromJson(gson, reader, JsonObject::class.java)!!
            }
        } catch (e: JsonParseException) {
            TCFoundation.logger.error("JSON parse exception", e)
            return false
        }

        try {
            val recipe = parser.process(jsonObject, jsonContext)
            loadedRecipes.put(type, recipe)
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
     * @param type type of recipe that is loaded
     */
    private fun parseMachineRecipes(endpoint: String, parser: RecipeParser<*>, type: RecipeType) {
        Loader.instance().indexedModList.forEach { (modId, modContainer) ->
            CraftingHelper.findFiles(
                    modContainer,
                    "assets/$modId/$RECIPE_ASSETS_FOLDER/$endpoint",
                    { true },
                    { _, path -> this.loadRecipe(path, parser, type) },
                    true,
                    true)
        }
    }

    /**
     * An enumeration of all custom recipe types parsed by the RecipeManager
     */
    enum class RecipeType {
        ALLOY,
        CENTRIFUGE,
        CHEMICAL_ETCHING,
        CHEMICAL_OXIDIZER,
        CHEMICAL_PROCESSING,
        CHEMICAL_REACTION,
        COMPACTOR,
        ELECTRIC_FURNACE,
        ELECTROLYSIS,
        KILN,
        PULVERIZER,
        REFINERY
    }
}