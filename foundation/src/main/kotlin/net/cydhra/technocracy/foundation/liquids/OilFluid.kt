package net.cydhra.technocracy.foundation.liquids

import net.cydhra.technocracy.foundation.liquids.general.AbstractFluid
import net.minecraftforge.fluids.FluidStack
import java.awt.Color


class OilFluid : AbstractFluid("oil", Color(30, 30, 30), opaqueTexture = true) {

    init {

    }

    override fun doesVaporize(fluidStack: FluidStack?): Boolean {
        return false
    }


}