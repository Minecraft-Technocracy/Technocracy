package net.cydhra.technocracy.foundation.api.fluids

import net.cydhra.technocracy.foundation.content.blocks.BaseLiquidBlock
import net.cydhra.technocracy.foundation.content.blocks.BlockManager
import net.cydhra.technocracy.foundation.content.fluids.BaseFluid
import net.cydhra.technocracy.foundation.content.fluids.BaseFluidPlaceable
import net.minecraft.block.material.Material
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry

class FluidManager(private val blockManager: BlockManager) {

    /**
     * Register a fluid at the FluidRegistry. Since fluids must be registered before their respective fluid blocks,
     * they are not prepared and registered alongside blocks in the block event, but directly in pre-init using this
     * method
     */

    fun registerFluid(fluid: Fluid) {
        FluidRegistry.addBucketForFluid(fluid)

        if (fluid is BaseFluidPlaceable) {
            blockManager.prepareBlocksForRegistration(
                BaseLiquidBlock(fluid, fluid.name, if (fluid.isGaseous)
                Material.AIR
            else Material.WATER)
            )
        }
        if (fluid is BaseFluid) {
            registerSecondaryFluid(fluid)
        }
    }

    fun registerSecondaryFluid(fluid: BaseFluid) {
        if (fluid.secondaryTemperature != null) {
            val hot = BaseFluid(fluid.name + "_hot", fluid.color, opaqueTexture = fluid.opaqueTexture,
                    isGas = fluid.isGaseous, temperature = fluid.secondaryTemperature)
            fluid.hotFluid = hot
            registerFluid(hot)
        }
    }
}