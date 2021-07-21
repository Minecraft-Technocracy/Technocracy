package net.cydhra.technocracy.foundation.content.items

import net.cydhra.technocracy.foundation.api.blocks.color.IBlockColor
import net.cydhra.technocracy.foundation.api.items.TCItem
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemArmor
import net.minecraftforge.common.ISpecialArmor

abstract class BaseArmorItem(unlocalizedName: String,
                             registryName: String = unlocalizedName,
                             material: ArmorMaterial,
                             renderIndex: Int,
                             equipmentSlot: EntityEquipmentSlot,
                             override val oreDictName: String? = null,
                             override val itemColor: IBlockColor? = null) : TCItem, ItemArmor(material, renderIndex, equipmentSlot) , ISpecialArmor {
    /**
     * A convenience property used during item registration to set the model location to the default assets path and
     * the registry name as file name. Can be overridden to use a different model
     */
    override val modelLocation: String
        get() = this.registryName.toString()

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}