package net.cydhra.technocracy.foundation.liquids

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import java.awt.Color


class OilFluid : Fluid("oil", ResourceLocation("technocracy.foundation", "block/steel"),
        ResourceLocation("technocracy.foundation", "block/steel_dark"), Color(46, 46, 46)) {

    init {

    }

    override fun doesVaporize(fluidStack: FluidStack?): Boolean {
        return false
    }


}