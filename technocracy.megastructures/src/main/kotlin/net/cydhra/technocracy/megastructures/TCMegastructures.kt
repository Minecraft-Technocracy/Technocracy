package net.cydhra.technocracy.megastructures

import net.minecraftforge.fml.common.Mod

@Mod(modid = TCMegastructures.MODID, name = TCMegastructures.NAME, version = TCMegastructures.VERSION,
        modLanguageAdapter = TCMegastructures.LANGUAGE_ADAPTER, dependencies = TCMegastructures.DEPENDENCIES)
object TCMegastructures {

    /**
     * Module-internal constant for the forge mod identifier
     */
    const val MODID = "technocracy.megastructures"

    /**
     * Mod version
     */
    const val VERSION = "1.0"

    /**
     * Mod name used by forge
     */
    const val NAME = "Technocracy Megastructures"

    /**
     * The adapter responsible to load this mod class, as it is not a default java mod class
     */
    internal const val LANGUAGE_ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"

    /**
     * All forge mod hard-dependencies that must be provided
     */
    internal const val DEPENDENCIES = "required-after:forgelin;" +
            "required-after:technocracy.foundation"
}
