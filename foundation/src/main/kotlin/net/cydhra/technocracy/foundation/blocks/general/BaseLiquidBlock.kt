package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumBlockRenderType
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fluids.Fluid

open class BaseLiquidBlock(fluid: Fluid,
                           unlocalizedName: String,
                           material: Material,
                           registryName: String = unlocalizedName,
                           mapColor: MapColor = material.materialMapColor,
                           override val colorMultiplier: ConstantBlockColor? = null)
    : BlockFluidClassic(fluid, material, mapColor), IBaseBlock {

    override val modelLocation: String = unlocalizedName

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }
}