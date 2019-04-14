package net.cydhra.technocracy.foundation.liquids.general

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry

object FluidManager {

    /**
     * Register a fluid at the FluidRegistry. Since fluids must be registered before their respective fluid blocks,
     * they are not prepared and registered alongside blocks in the block event, but directly in pre-init using this
     * method
     */
    fun registerFluid(fluid: Fluid) {
        FluidRegistry.registerFluid(fluid)
        FluidRegistry.addBucketForFluid(fluid)
    }
}