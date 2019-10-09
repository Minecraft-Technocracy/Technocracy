package net.cydhra.technocracy.foundation.crafting

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.crafting.RecipeManager.RECIPE_ASSETS_FOLDER
import net.cydhra.technocracy.foundation.crafting.special.HeatRecipeParser
import net.cydhra.technocracy.foundation.crafting.types.*
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.machines.*
import net.minecraft.block.Block
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
     * All machine recipes loaded by type
     */
    private val loadedMachineRecipes: Multimap<RecipeType, IMachineRecipe> = HashMultimap.create()

    /**
     * Loaded non-machine recipes by type
     */
    private val loadedSpecialRecipes: Multimap<RecipeType, ISpecialRecipe> = HashMultimap.create()

    /**
     * Called upon post initialization by common proxy. Attempts to load recipes of all different machine types.
     */
    fun initialize() {
        parseMachineRecipes("alloy_smeltery", MITIRecipeParser, loadedMachineRecipes, RecipeType.ALLOY)
        parseMachineRecipes("centrifuge", ITMIRecipeParser, loadedMachineRecipes, RecipeType.CENTRIFUGE)
        parseMachineRecipes("chemical_etching", MITIRecipeParser, loadedMachineRecipes, RecipeType.CHEMICAL_ETCHING)
        parseMachineRecipes("chemical_oxidizer", ITFRecipeParser, loadedMachineRecipes, RecipeType.CHEMICAL_OXIDIZER)
        parseMachineRecipes("chemical_processing", IFTIRecipeParser, loadedMachineRecipes, RecipeType.CHEMICAL_PROCESSING)
        parseMachineRecipes("chemical_reaction", MFTFRecipeParser, loadedMachineRecipes, RecipeType.CHEMICAL_REACTION)
        parseMachineRecipes("compactor", ITIRecipeParser, loadedMachineRecipes, RecipeType.COMPACTOR)
        parseMachineRecipes("crystallization", FTIRecipeParser, loadedMachineRecipes, RecipeType.CRYSTALLIZATION)
        parseMachineRecipes("dissolution", IFTFRecipeParser, loadedMachineRecipes, RecipeType.DISSOLUTION)
        parseMachineRecipes("electric_furnace", ITIRecipeParser, loadedMachineRecipes, RecipeType.ELECTRIC_FURNACE)
        parseMachineRecipes("electrolysis", FTMFRecipeParser, loadedMachineRecipes, RecipeType.ELECTROLYSIS)
        parseMachineRecipes("kiln", FTFRecipeParser, loadedMachineRecipes, RecipeType.KILN)
        parseMachineRecipes("polymerization", FTIRecipeParser, loadedMachineRecipes, RecipeType.POLYMERIZATION)
        parseMachineRecipes("pulverizer", ITIRecipeParser, loadedMachineRecipes, RecipeType.PULVERIZER)
        parseMachineRecipes("refinery", FTMFRecipeParser, loadedMachineRecipes, RecipeType.REFINERY)

        parseMachineRecipes("heat", HeatRecipeParser, loadedSpecialRecipes, RecipeType.HEAT)
    }

    /**
     * Query all machine recipes of a specific type
     *
     * @param type request recipe type
     *
     * @return an immutable collection of registered recipes of the specified type
     */
    fun getMachineRecipesByType(type: RecipeType): Collection<IMachineRecipe>? {
        return loadedMachineRecipes[type]
    }

    /**
     * Add a recipe outside from the recipe json model data. Be aware that the usage of this function is unsafe, as
     * incompatible recipe types can be registered, which will later cause [ClassCastException]s when trying to parse
     * them.
     *
     * @param type recipe type
     * @param recipe recipe model
     */
    fun registerRecipe(type: RecipeType, recipe: IMachineRecipe) {
        loadedMachineRecipes.put(type, recipe)
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
    private fun <T : ISpecialRecipe> loadRecipe(path: Path,
                                                parser: RecipeParser<out T>,
                                                target: Multimap<RecipeType, T>,
                                                type: RecipeType): Boolean {
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
            target.put(type, recipe)
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
    private fun <T : ISpecialRecipe> parseMachineRecipes(endpoint: String,
                                                         parser: RecipeParser<out T>,
                                                         target: Multimap<RecipeType, T>,
                                                         type: RecipeType) {
        Loader.instance().indexedModList.forEach { (modId, modContainer) ->
            CraftingHelper.findFiles(
                    modContainer,
                    "assets/$modId/$RECIPE_ASSETS_FOLDER/$endpoint",
                    { true },
                    { _, path -> this.loadRecipe(path, parser, target, type) },
                    true,
                    true)
        }
    }

    /**
     * An enumeration of all custom recipe types parsed by the RecipeManager
     * @param machineBlock used for jei integration; might be null if machine which belongs to this recipe type is manually handled (f.e. refinery)
     * @param tileEntityClass used for jei integration; might be null if machine which belongs to this recipe type is manually handled (f.e. refinery)
     */
    enum class RecipeType(val machineBlock: Block?, val tileEntityClass: Class<out MachineTileEntity>?) {
        ALLOY(alloySmelteryBlock, TileEntityAlloySmeltery::class.java),
        CENTRIFUGE(centrifugeBlock, TileEntityCentrifuge::class.java),
        CHEMICAL_ETCHING(chemicalEtchingChamberBlock, TileEntityChemicalEtchingChamber::class.java),
        CHEMICAL_OXIDIZER(chemicalOxidizerBlock, TileEntityChemicalOxidizer::class.java),
        CHEMICAL_PROCESSING(chemicalProcessingChamberBlock, TileEntityChemicalProcessingChamber::class.java),
        CHEMICAL_REACTION(chemicalReactionChamberBlock, TileEntityChemicalReactionChamber::class.java),
        COMPACTOR(compactorBlock, TileEntityCompactor::class.java),
        CRYSTALLIZATION(crystallizationChamberBlock, TileEntityCrystallizationChamber::class.java),
        DISSOLUTION(dissolutionChamberBlock, TileEntityDissolutionChamber::class.java),
        ELECTRIC_FURNACE(electricFurnaceBlock, TileEntityElectricFurnace::class.java),
        ELECTROLYSIS(electrolysisChamberBlock, TileEntityElectrolysisChamber::class.java),
        KILN(kilnBlock, TileEntityKiln::class.java),
        POLYMERIZATION(polymerizationChamberBlock, TileEntityPolymerizationChamber::class.java),
        PULVERIZER(pulverizerBlock, TileEntityPulverizer::class.java),
        REFINERY(null, null),

        HEAT(null, null)
    }
}