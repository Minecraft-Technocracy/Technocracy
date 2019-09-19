package net.cydhra.technocracy.foundation.liquids.general

import java.awt.Color


val mineralOilFluid = BaseFluid("mineral_oil", Color(25, 25, 25), opaqueTexture = true, secondaryTemperature = 700)
        .apply {
    this.viscosity = 3000
    this.density = 900
}
val sulfurDioxideFluid = BaseFluid("sulfur_dioxide", Color(255, 230, 104), opaqueTexture = false)
val oxygenFluid = BaseFluid("oxygen", Color(243, 255, 255), opaqueTexture = false)
val sulfurTrioxideFluid = BaseFluid("sulfur_trioxide", Color(255, 210, 177), opaqueTexture = false)
val sulfuricAcidFluid = BaseFluid("sulfuric_acid", Color(255, 230, 104), opaqueTexture = false)
val propeneFluid = BaseFluid("propene", Color(255, 212, 91), opaqueTexture = false)
val acrylicAcidFluid = BaseFluid("acrylic_acid", Color(255, 116, 148), opaqueTexture = false)
val benzeneFluid = BaseFluid("benzene", Color(93, 93, 186), opaqueTexture = false)
val phenolFluid = BaseFluid("phenol", Color(117, 123, 225), opaqueTexture = false)
val keroseneFluid = BaseFluid("kerosene", Color(221, 225, 168), opaqueTexture = false)
val rocketFuelFluid = BaseFluid("rocket_fuel", Color(225, 118, 97), opaqueTexture = false)
val propyleneOxideFluid = BaseFluid("propylene_oxide", Color(225, 185, 210), opaqueTexture = false)
val chlorineFluid = BaseFluid("chlorine", Color(120, 200, 120), opaqueTexture = false)
val styreneFluid = BaseFluid("styrene", Color(255, 255, 255), opaqueTexture = false)
val cryogenicGelFluid = BaseFluid("cryogenic_gel", Color(110, 110, 200), opaqueTexture = true)
val heavyOilFluid = BaseFluid("heavy_oil", Color(25, 25, 25), opaqueTexture = true, secondaryTemperature = 700)
val lightOilFluid = BaseFluid("light_oil", Color(25, 25, 25), opaqueTexture = true, secondaryTemperature = 700)
val tarFluid = BaseFluid("tar", Color(30, 30, 30), opaqueTexture = true)
val pitchFluid = BaseFluid("pitch", Color(41, 41, 41), opaqueTexture = true)
val hydrochloricAcidFluid = BaseFluid("hydrochloric_acid", Color(130, 255, 141), opaqueTexture = false)
val hydrogenFluid = BaseFluid("hydrogen", Color(215, 255, 247), opaqueTexture = false)
val silicaFluid = BaseFluid("silica", Color(241, 230, 255), opaqueTexture = false)
val steamFluid = BaseFluid("steam", Color(160, 160, 160), opaqueTexture = false, isGas = true, temperature = 380)
val drossFluid = BaseFluid("dross", Color(100, 100, 100), opaqueTexture = true)