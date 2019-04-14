package net.cydhra.technocracy.foundation.liquids

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import java.awt.Color


class FluidOil : Fluid("oil", ResourceLocation("technocracy.foundation", "steel"), ResourceLocation("technocracy" +
        ".foundation", "steel_dark"), Color(46,46,46)) {

    init {
        //setBlock(oilSandBlock)
    }
}