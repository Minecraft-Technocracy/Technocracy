package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.general.AbstractBaseLiquid
import net.cydhra.technocracy.foundation.liquids.general.oil
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumBlockRenderType
import net.minecraftforge.client.model.ModelLoader


class OilBlock : AbstractBaseLiquid(oil, "oil_block", Material.WATER) {

    val location: ModelResourceLocation = ModelResourceLocation(TCFoundation.MODID + ":" + "fluid", "oil")

    init {
        fluid.block = this
        ModelLoader.setCustomStateMapper(this, CustomStateMapper())
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    inner class CustomStateMapper : StateMapperBase(), ItemMeshDefinition {
        override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
            return location
        }

        override fun getModelLocation(stack: ItemStack): ModelResourceLocation {
            return location
        }
    }
}