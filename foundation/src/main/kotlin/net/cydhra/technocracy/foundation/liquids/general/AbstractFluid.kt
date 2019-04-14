package net.cydhra.technocracy.foundation.liquids.general

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import java.awt.Color


abstract class AbstractFluid(fluidName: String, color: Color, opaqueTexture: Boolean) : Fluid(fluidName,
        ResourceLocation("technocracy.foundation", "liquid/fluid_${if (opaqueTexture) "opaque_" else ""}still"),
        ResourceLocation("technocracy.foundation", "liquid/fluid_${if (opaqueTexture) "opaque_" else ""}flow"),
        color)