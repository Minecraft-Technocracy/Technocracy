package net.cydhra.technocracy.foundation.liquids.general

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.BaseLiquidBlock
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.minecraft.block.material.Material
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
object FluidManager {

    /**
     * Register a fluid at the FluidRegistry. Since fluids must be registered before their respective fluid blocks,
     * they are not prepared and registered alongside blocks in the block event, but directly in pre-init using this
     * method
     */

    fun registerFluid(fluid: Fluid) {
        FluidRegistry.addBucketForFluid(fluid)

        if (fluid is BaseFluidPlaceable) {
            BlockManager.prepareBlocksForRegistration(BaseLiquidBlock(fluid, fluid.name, if (fluid.isGaseous)
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