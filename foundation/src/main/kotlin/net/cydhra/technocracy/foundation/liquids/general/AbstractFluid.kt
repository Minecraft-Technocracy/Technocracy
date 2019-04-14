package net.cydhra.technocracy.foundation.liquids.general

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid


abstract class AbstractFluid(fluidName: String) : Fluid(fluidName, ResourceLocation("technocracy.foundation",
        "liquids/${fluidName}_still"), ResourceLocation("technocracy.foundation",
        "liquids/${fluidName}_flowing")) {
}