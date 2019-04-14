package net.cydhra.technocracy.foundation.liquids.general

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry


object FluidManager {

    fun registerFluid(fluid: Fluid) {
        FluidRegistry.registerFluid(fluid)
        FluidRegistry.addBucketForFluid(fluid)
    }
}