package net.cydhra.technocracy.foundation.model.fluids.api

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import java.awt.Color

/**
 * Abstract base class for all fluids added by this modification. They automatically get fluid textures assigned
 * which are manipulated through a color
 *
 * @param fluidName unlocalized and registry name for fluid
 * @param color color modifier for fluid textures (also for universal bucket texture)
 * @param opaqueTexture whether to use the opaque or the transparent fluid texture
 */
open class BaseFluid(fluidName: String, val color: Color, temperature: Int = 300,
                     val opaqueTexture: Boolean, isGas: Boolean = false, val secondaryTemperature: Int? = null)
    : Fluid(fluidName, ResourceLocation("technocracy.foundation",
        "liquid/fluid_${if (opaqueTexture) "opaque_" else ""}still"),
        ResourceLocation("technocracy.foundation", "liquid/fluid_${if (opaqueTexture) "opaque_" else ""}flow"),
        color) {

    lateinit var hotFluid: Fluid

    init {
        this.isGaseous = isGas
        this.temperature = temperature
    }
}