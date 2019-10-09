package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.content.fluids.sulfuricAcidFluid
import net.cydhra.technocracy.foundation.model.blocks.impl.BaseLiquidBlock
import net.minecraft.block.material.Material

class SulfuricAcidBlock : BaseLiquidBlock(sulfuricAcidFluid,"sulfuric_acid", Material.WATER)