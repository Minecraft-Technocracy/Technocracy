package net.cydhra.technocracy.foundation.model.fluids.api

import java.awt.Color


class BaseFluidPlaceable(fluidName: String, color: Color, opaqueTexture: Boolean) : BaseFluid(fluidName, color,
        opaqueTexture = opaqueTexture)