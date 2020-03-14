@file:Suppress("unused")

package net.cydhra.technocracy.foundation.model.oresystems.api

import com.google.common.base.Predicate
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.content.blocks.OreBlock
import net.cydhra.technocracy.foundation.content.fluids.drossFluid
import net.cydhra.technocracy.foundation.content.fluids.hydrochloricAcidFluid
import net.cydhra.technocracy.foundation.content.fluids.hydrogenFluid
import net.cydhra.technocracy.foundation.content.fluids.lyeFluid
import net.cydhra.technocracy.foundation.data.config.BooleanConfigurable
import net.cydhra.technocracy.foundation.data.config.IntegerConfigurable
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.data.crafting.types.*
import net.cydhra.technocracy.foundation.model.blocks.manager.BlockManager
import net.cydhra.technocracy.foundation.model.fluids.api.BaseFluid
import net.cydhra.technocracy.foundation.model.fluids.manager.FluidManager
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.cydhra.technocracy.foundation.model.items.api.ColoredPrefixedItem
import net.cydhra.technocracy.foundation.model.items.color.ConstantItemColor
import net.cydhra.technocracy.foundation.model.items.manager.ItemManager
import net.cydhra.technocracy.foundation.model.world.api.OreGenerator
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.block.state.pattern.BlockMatcher
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.ResourceLocation
import net.minecraft.world.DimensionType
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.registry.GameRegistry
import java.awt.Color

fun oreSystem(block: OreSystemBuilder.() -> Unit) = OreSystemBuilder().apply(block).build()

class OreSystem(val materialName: String, val oreType: String, val ore: Block, val ingot: Item, val dust: Item, val crystal: ColoredPrefixedItem,
                val grit: ColoredPrefixedItem, val gear: ColoredPrefixedItem?, val sheet: ColoredPrefixedItem?, val slag: BaseFluid,
                val slurry: BaseFluid, val enrichedSlurry: BaseFluid,
                val oreGeneratorSettings: OreSystemBuilder.OreGeneratorSettings,
                val preInit: OreSystem.(BlockManager, ItemManager, FluidManager) -> Unit, val init: OreSystem.() -> Unit) {
    private val oreSystemCategory = "ores${Configuration.CATEGORY_SPLITTER}$materialName"

    val oreEnabled by BooleanConfigurable(TCFoundation.config,
            oreSystemCategory,
            "enabled",
            true,
            "whether $materialName ore is generated in worlds.")

    val oreVeinsPerChunk by IntegerConfigurable(TCFoundation.config,
            oreSystemCategory,
            "veinsPerChunk",
            oreGeneratorSettings.veinsPerChunk,
            "how many ore veins are generated per chunk",
            1,
            24)


    val blocksPerVein by IntegerConfigurable(TCFoundation.config,
            oreSystemCategory,
            "blocksPerVein",
            oreGeneratorSettings.amountPerVein,
            "how many ore blocks are generated per vein",
            1,
            24)

    val minOreHeight by IntegerConfigurable(TCFoundation.config,
            oreSystemCategory,
            "minHeight",
            oreGeneratorSettings.minHeight,
            "minimum height at which ores are generated",
            0,
            127)

    val maxOreHeight by IntegerConfigurable(TCFoundation.config,
            oreSystemCategory,
            "maxHeight",
            oreGeneratorSettings.maxHeight,
            "maximum height at which ores are generated",
            0,
            127)
}

class OreSystemBuilder {
    lateinit var name: String
    var color: Int = 0

    var oreType: String = "ore"

    private var generateOre = true
    private var generateIngot = true
    private var generateDust = true
    private var intermediates: Array<out IntermediateProductType> = emptyArray()

    private lateinit var ore: Block
    private lateinit var ingot: Item
    private lateinit var dust: Item

    private lateinit var oreGeneratorSettings: OreGeneratorSettings

    /**
     * Import the ore block from elsewhere instead of creating own ore block. This prevents any registration of
     * ores in the world
     */
    fun importOre(ore: Block) {
        this.generateOre = false
        this.ore = ore
    }

    /**
     * Import the ingot item from elsewhere instead of creating own ingot item.
     */
    fun importIngot(ingot: Item) {
        this.generateIngot = false
        this.ingot = ingot
    }

    /**
     * Import the dust item from elsewhere instead of creating own dust item.
     */
    fun importDust(dust: Item) {
        this.generateDust = false
        this.dust = dust
    }

    /**
     * Overwrite the set of intermediate production goods generated of this ore type.
     */
    fun create(vararg types: IntermediateProductType) {
        this.intermediates = types
    }

    fun generate(settings: OreGeneratorSettings.() -> Unit) {
        this.oreGeneratorSettings = OreGeneratorSettings().apply(settings)
    }

    /**
     * Build an ore system from all settings given
     */
    fun build(): OreSystem {
        val itemColor = ConstantItemColor(this.color)

        if (generateOre) {
            this.ore = OreBlock(this.name, this.oreType, this.color)
        } else {
            // dummy settings. if no ore is generated, those will not be used
            this.oreGeneratorSettings = OreGeneratorSettings()
        }

        if (generateIngot) this.ingot = ColoredPrefixedItem("ingot", this.name, itemColor, true)

        if (generateDust) this.dust = ColoredPrefixedItem("dust", this.name, itemColor, true)

        val sheet = if (IntermediateProductType.SHEET in intermediates) ColoredPrefixedItem("sheet", this.name, itemColor, true)
        else null

        val gear = if (IntermediateProductType.GEAR in intermediates) ColoredPrefixedItem("gear", this.name, itemColor, true)
        else null

        return OreSystem(materialName = this.name,
                oreType = this.oreType,
                ore = this.ore,
                ingot = this.ingot,
                dust = this.dust,
                crystal = ColoredPrefixedItem("crystal", this.name, itemColor, true),
                grit = ColoredPrefixedItem("grit", this.name, itemColor, true),
                gear = gear,
                sheet = sheet,
                slag = BaseFluid("slag.$name", Color(this.color), opaqueTexture = true),
                slurry = BaseFluid("slurry.$name", Color(this.color).darker(), opaqueTexture = true),
                enrichedSlurry = BaseFluid("enriched_slurry.$name",
                        Color(this.color).darker().darker(),
                        opaqueTexture = true),
                oreGeneratorSettings = oreGeneratorSettings,
                preInit = { blockManager, itemManager, fluidManager ->
                    if (this.ingot is BaseItem) itemManager.prepareItemForRegistration(this.ingot)
                    if (this.dust is BaseItem) itemManager.prepareItemForRegistration(this.dust)
                    if (this.sheet != null) itemManager.prepareItemForRegistration(this.sheet)
                    if (this.gear != null) itemManager.prepareItemForRegistration(this.gear)

                    itemManager.prepareItemForRegistration(this.crystal)
                    itemManager.prepareItemForRegistration(this.grit)

                    if (this.ore is OreBlock) blockManager.prepareBlocksForRegistration(this.ore)

                    fluidManager.registerFluid(this.slag)
                    fluidManager.registerFluid(this.slurry)
                    fluidManager.registerFluid(this.enrichedSlurry)
                },
                init = {
                    // add default ingot recipe
                    GameRegistry.addSmelting(ore, ItemStack(ingot, 1), 0.5f)

                    // add pulverizer recipes
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.PULVERIZER,
                            ITIRecipe(Ingredient.fromItem(Item.getItemFromBlock(ore)),
                                    ItemStack(dust, 2),
                                    60))

                    RecipeManager.registerRecipe(RecipeManager.RecipeType.PULVERIZER,
                            ITIRecipe(Ingredient.fromItem(ingot),
                                    ItemStack(dust, 1),
                                    30))

                    // add dust smelting recipe
                    GameRegistry.addSmelting(dust, ItemStack(ingot, 1), 0.5f)

                    // add slag recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.DISSOLUTION,
                            IFTFRecipe(Ingredient.fromItem(Item.getItemFromBlock(ore)),
                                    FluidStack(hydrochloricAcidFluid, 250),
                                    FluidStack(slag, 1000),
                                    80))

                    // add slurry recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.ELECTROLYSIS,
                            FTMFRecipe(FluidStack(slag, 1000),
                                    listOf(FluidStack(slurry, 500), FluidStack(drossFluid, 500)),
                                    200))

                    // add slurry enriching recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.KILN,
                            FTFRecipe(FluidStack(slurry, 500), FluidStack(enrichedSlurry, 500), 200))

                    // add crystal recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.CRYSTALLIZATION,
                            FTIRecipe(FluidStack(enrichedSlurry, 250), ItemStack(crystal), 200))

                    // add grit recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.INDUSTRIAL_REFINERY,
                            MIMFTIRecipe(
                                    listOf(Ingredient.fromItem(crystal),
                                            Ingredient.fromItem(Item.getItemFromBlock(Blocks.GRAVEL))),
                                    listOf(FluidStack(lyeFluid, 80), FluidStack(hydrogenFluid, 20)),
                                    ItemStack(grit, 2), 160))

                    // add dust recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.CENTRIFUGE,
                            ITMIRecipe(Ingredient.fromItem(grit),
                                    listOf(ItemStack(dust, 1), ItemStack(Blocks.GRAVEL, 1)), 100))

                    // add gear recipe
                    if (gear != null) GameRegistry.addShapedRecipe(ResourceLocation(TCFoundation.MODID,
                            gear.registryName!!.resourcePath + "_recipe"),
                            null,
                            ItemStack(gear),
                            " # ",
                            "# #",
                            " # ",
                            '#',
                            ingot)

                    // add sheet recipe
                    if (sheet != null) RecipeManager.registerRecipe(RecipeManager.RecipeType.COMPACTOR,
                            ITIRecipe(Ingredient.fromItem(ingot), ItemStack(sheet), 40))

                    if (generateOre) {
                        if (oreEnabled) {
                            GameRegistry.registerWorldGenerator(OreGenerator(oreGeneratorSettings.oreDimensions,
                                    oreGeneratorSettings.replacementPredicate,
                                    this.ore.defaultState,
                                    this.oreVeinsPerChunk,
                                    this.blocksPerVein,
                                    this.minOreHeight,
                                    this.maxOreHeight), 0)
                        }
                    }
                })
    }

    /**
     * An enumeration of all intermediate products that can be obtained from metals
     */
    enum class IntermediateProductType {
        GEAR, SHEET
    }

    /**
     * Settings for ore generation, if the ore is not imported from elsewhere
     */
    class OreGeneratorSettings {
        var oreDimensions: Array<Int> = arrayOf(DimensionType.OVERWORLD.id)
        var replacementPredicate: Predicate<IBlockState> = BlockMatcher.forBlock(Blocks.STONE)
        var veinsPerChunk = 8
        var amountPerVein = 6
        var minHeight = 0
        var maxHeight = 64
    }
}
