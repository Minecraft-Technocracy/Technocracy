package net.cydhra.technocracy.astronautics.client.model.concreteSprayer

import net.cydhra.technocracy.astronautics.items.ConcreteSprayerItem
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import java.util.*


class ConcreteSprayerRedirector(val emptySprayer: IBakedModel, val filledSprayer: IBakedModel) : IBakedModel by emptySprayer {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        return Collections.emptyList()
    }

    override fun getOverrides(): ItemOverrideList {
        return object : ItemOverrideList(emptyList<ItemOverride>()) {
            override fun handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World?, entity: EntityLivingBase?): IBakedModel {
                if (stack.item !is ConcreteSprayerItem)
                    return emptySprayer

                if ((stack.item as ConcreteSprayerItem).getConcreteType(stack) != null) {
                    return filledSprayer
                }

                return emptySprayer
            }
        }
    }
}