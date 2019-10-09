package net.cydhra.technocracy.foundation.model.fluids.manager

import net.cydhra.technocracy.foundation.model.blocks.impl.BaseLiquidBlock
import net.cydhra.technocracy.foundation.model.fluids.api.BaseFluid
import net.cydhra.technocracy.foundation.model.fluids.api.BaseFluidPlaceable
import net.cydhra.technocracy.foundation.model.blocks.manager.BlockManager
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
            blockManager.prepareBlocksForRegistration(BaseLiquidBlock(fluid, fluid.name, if (fluid.isGaseous)
                Material.AIR
            else Material.WATER))
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