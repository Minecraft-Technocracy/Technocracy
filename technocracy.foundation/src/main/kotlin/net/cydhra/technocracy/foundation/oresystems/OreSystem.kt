@file:Suppress("unused")

package net.cydhra.technocracy.foundation.oresystems

import com.google.common.base.Predicate
import net.cydhra.technocracy.foundation.blocks.OreBlock
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.items.color.ConstantItemColor
import net.cydhra.technocracy.foundation.items.general.BaseItem
import net.cydhra.technocracy.foundation.items.general.ColoredItem
import net.cydhra.technocracy.foundation.items.general.ItemManager
import net.cydhra.technocracy.foundation.liquids.general.BaseFluid
import net.cydhra.technocracy.foundation.liquids.general.FluidManager
import net.cydhra.technocracy.foundation.world.gen.OreGenerator
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.block.state.pattern.BlockMatcher
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.common.registry.GameRegistry
import java.awt.Color

fun oreSystem(block: OreSystemBuilder.() -> Unit) = OreSystemBuilder().apply(block).build()

class OreSystem(
        val materialName: String,
        val ore: Block,
        val ingot: Item,
        val dust: Item,
        val gear: ColoredItem?,
        val sheet: ColoredItem?,
        val slag: BaseFluid,
        val preInit: OreSystem.(BlockManager, ItemManager, FluidManager) -> Unit,
        val init: OreSystem.() -> Unit)

class OreSystemBuilder {
    lateinit var name: String
    var color: Int = 0

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

        if (generateOre)
            this.ore = OreBlock(this.name, this.color)

        if (generateIngot)
            this.ingot = ColoredItem("ingot", this.name, itemColor)

        if (generateDust)
            this.dust = ColoredItem("dust", this.name, itemColor)

        val sheet = if (IntermediateProductType.SHEET in intermediates)
            ColoredItem("sheet", this.name, itemColor)
        else
            null

        val gear = if (IntermediateProductType.GEAR in intermediates)
            ColoredItem("gear", this.name, itemColor)
        else
            null

        return OreSystem(
                materialName = this.name,
                ore = this.ore,
                ingot = this.ingot,
                dust = this.dust,
                gear = gear,
                sheet = sheet,
                slag = BaseFluid("slag.$name", Color(this.color), opaqueTexture = true),
                preInit = { blockManager, itemManager, fluidManager ->
                    if (this.ingot is BaseItem)
                        itemManager.prepareItemForRegistration(this.ingot)
                    if (this.dust is BaseItem)
                        itemManager.prepareItemForRegistration(this.dust)
                    if (this.sheet != null)
                        itemManager.prepareItemForRegistration(this.sheet)
                    if (this.gear != null)
                        itemManager.prepareItemForRegistration(this.gear)
                    if (this.ore is OreBlock)
                        blockManager.prepareBlocksForRegistration(this.ore)

                    fluidManager.registerFluid(this.slag)
                },
                init = {
                    if (generateOre)
                        GameRegistry.addSmelting(ore, ItemStack(ingot, 1), 0.5f)
                    if (generateDust)
                        GameRegistry.addSmelting(dust, ItemStack(ingot, 1), 0.5f)

                    if (generateOre)
                        GameRegistry.registerWorldGenerator(OreGenerator(
                                oreGeneratorSettings.oreDimensions,
                                oreGeneratorSettings.replacementPredicate,
                                this.ore.defaultState,
                                oreGeneratorSettings.veinsPerChunk,
                                oreGeneratorSettings.amountPerVein,
                                oreGeneratorSettings.minHeight,
                                oreGeneratorSettings.maxHeight), 0)
                }
        )
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
