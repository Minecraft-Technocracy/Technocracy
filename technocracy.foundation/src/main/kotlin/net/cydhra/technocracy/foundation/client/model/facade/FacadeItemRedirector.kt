package net.cydhra.technocracy.foundation.client.model.facade

import net.cydhra.technocracy.foundation.items.general.FacadeItem
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverride
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import java.util.*


class FacadeItemRedirector(val baseBakedModel: IBakedModel) : IBakedModel by baseBakedModel {

    companion object {
        private val cache = mutableMapOf<Int, FacadeItemRedirector>()
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        return Collections.emptyList()
    }

    override fun getOverrides(): ItemOverrideList {
        return object : ItemOverrideList(emptyList<ItemOverride>()) {
            override fun handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World?, entity: EntityLivingBase?): IBakedModel {
                if (stack.item !is FacadeItem)
                    return originalModel

                val facade = stack.item as FacadeItem
                val facadeBlock = facade.getFacadeFromStack(stack)

                if (facadeBlock.isEmpty)
                    return originalModel

                val hash = Objects.hash(stack.tagCompound)

                return cache.getOrPut(hash) {
                    //return Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(facadeBlock, null, null)
                    return FacadeItemBakedModel(baseBakedModel, facadeBlock)
                }
            }
        }
    }
}