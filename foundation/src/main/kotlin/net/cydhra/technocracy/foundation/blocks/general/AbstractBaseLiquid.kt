package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fluids.Fluid

abstract class AbstractBaseLiquid(fluid: Fluid,
                                  unlocalizedName: String,
                                  material: Material,
                                  registryName: String = unlocalizedName,
                                  mapColor: MapColor = material.materialMapColor,
                                  override val colorMultiplier: ConstantBlockColor? = null)
    : BlockFluidClassic(fluid, material, mapColor), IBaseBlock {

    override val modelLocation: String
        get() = this.registryName.toString()

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
    }
}