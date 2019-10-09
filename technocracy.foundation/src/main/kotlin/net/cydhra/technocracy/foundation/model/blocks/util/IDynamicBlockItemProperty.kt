package net.cydhra.technocracy.foundation.model.blocks.util

import net.minecraft.item.IItemPropertyGetter
import net.minecraft.util.ResourceLocation


interface IDynamicBlockItemProperty {
    fun getOverrides(): Map<ResourceLocation, IItemPropertyGetter>
}