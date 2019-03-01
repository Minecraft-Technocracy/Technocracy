package net.cydhra.technocracy.foundation.materials

import net.cydhra.technocracy.foundation.blocks.BlockManager
import net.cydhra.technocracy.foundation.blocks.OreBlock
import net.cydhra.technocracy.foundation.items.ConstantItemColor
import net.cydhra.technocracy.foundation.items.DustItem
import net.cydhra.technocracy.foundation.items.IngotItem
import net.cydhra.technocracy.foundation.items.ItemManager
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry



/**
 * A material system consists of an ore type, an ingot and all stages of ore processing necessary.
 *
 * @param materialName name of the mineable material this system introduces
 * @param colorMultiplier the color of the material states
 */
class MaterialSystem(materialName: String, colorMultiplier: Int) {

    /**
     * The ingot of this material
     */
    val ingot = IngotItem(materialName, ConstantItemColor(colorMultiplier))

    /**
     * The ore of this material
     */
    val ore = OreBlock(materialName, colorMultiplier)

    /**
     * the dusted ore that can be smelted into ingots
     */
    val dust = DustItem(materialName, ConstantItemColor(colorMultiplier))

    /**
     * Must be called in pre-init. Registers all the components at forge
     */
    fun preInit() {
        ItemManager.prepareItemForRegistration(this.ingot)
        ItemManager.prepareItemForRegistration(this.dust)
        BlockManager.prepareBlocksForRegistration(this.ore)
    }

    fun init() {
        GameRegistry.addSmelting(ore, ItemStack(ingot, 1), 0.5f)
        GameRegistry.addSmelting(dust, ItemStack(ingot, 1), 0.5f)
    }
}