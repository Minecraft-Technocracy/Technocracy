package net.cydhra.technocracy.megastructures.dyson

import net.minecraft.util.math.MathHelper

object DysonSphereController {

    //amount between 0 and 1, 1 is fully build sphere
    var sphereAmount = 0f
        get() {
            return MathHelper.clamp(field, 0f, 1f)
        }
}