package net.cydhra.technocracy.foundation.liquids

import net.cydhra.technocracy.foundation.liquids.general.AbstractFluid
import net.minecraftforge.fluids.FluidStack
import java.awt.Color


class OilFluid : AbstractFluid("oil", Color(25, 25, 25), opaqueTexture = true) {

    init {

    }

    override fun doesVaporize(fluidStack: FluidStack?): Boolean {
        return false
    }


}