package net.cydhra.technocracy.foundation.materialsystems

import com.google.common.base.Predicate
import net.cydhra.technocracy.foundation.blocks.OreBlock
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.items.color.ConstantItemColor
import net.cydhra.technocracy.foundation.items.general.ColoredItem
import net.cydhra.technocracy.foundation.items.general.ItemManager
import net.cydhra.technocracy.foundation.world.gen.OreGenerator
import net.minecraft.block.state.IBlockState
import net.minecraft.block.state.pattern.BlockMatcher
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.world.DimensionType
import net.minecraftforge.fml.common.registry.GameRegistry


/**
 * A material system consists of an ore type, an ingot and all stages of ore processing necessary.
 *
 * @param materialName name of the mineable material this system introduces
 * @param colorMultiplier the color of the material states
 * @param oreDimensions the dimensions where ores are generated. Only [overworld][DimensionType.OVERWORLD] by default
 * @param replacementPredicate which blocks to replace. Only stone to replace
 * @param veinsPerChunk how many veins per chunk
 * @param
 */
class MaterialSystem(materialName: String, colorMultiplier: Int,
                     private val oreDimensions: Array<Int> = arrayOf(DimensionType.OVERWORLD.id),
                     private val replacementPredicate: Predicate<IBlockState> = BlockMatcher.forBlock(Blocks.STONE),
                     private val veinsPerChunk: Int, private val amountPerVein: Int, private val minHeight: Int,
                     private val maxHeight: Int) {

    /**
     * The color multiplier used for items of this material system
     */
    private val itemColor = ConstantItemColor(colorMultiplier)

    /**
     * The ingot of this material
     */
    val ingot = ColoredItem("ingot", materialName, itemColor)

    /**
     * The ore of this material
     */
    val ore = OreBlock(materialName, colorMultiplier)

    /**
     * the dusted ore that can be smelted into ingots
     */
    val dust = ColoredItem("dust", materialName, itemColor)

    /**
     * the compressed sheet item from the ingot
     */
    val sheet = ColoredItem("sheet", materialName, itemColor)

    /**
     * Normal gear of this system. Crafted from workbench
     */
    val gear = ColoredItem("gear", materialName, itemColor)

    /**
     * Must be called in pre-init. Registers all the components at forge
     */
    fun preInit(blockManager: BlockManager, itemManager: ItemManager) {
        itemManager.prepareItemForRegistration(this.ingot)
        itemManager.prepareItemForRegistration(this.dust)
        itemManager.prepareItemForRegistration(this.sheet)
        itemManager.prepareItemForRegistration(this.gear)
        blockManager.prepareBlocksForRegistration(this.ore)
    }

    fun init() {
        GameRegistry.addSmelting(ore, ItemStack(ingot, 1), 0.5f)
        GameRegistry.addSmelting(dust, ItemStack(ingot, 1), 0.5f)
        GameRegistry.registerWorldGenerator(OreGenerator(oreDimensions, replacementPredicate, this.ore.defaultState,
                this.veinsPerChunk, this.amountPerVein, this.minHeight, this.maxHeight), 0)
    }
}