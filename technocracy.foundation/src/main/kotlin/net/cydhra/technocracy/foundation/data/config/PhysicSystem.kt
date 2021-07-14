package net.cydhra.technocracy.foundation.data.config

import net.minecraftforge.common.config.Configuration


/**
 * A set of in-game-physics constants that can be configured by users. Those need to be referenced in code,
 * whenever calculations based on physical assumptions are made
 */
class PhysicSystem(private val config: Configuration) {
    val milliHeatPerRf by IntegerConfigurable(
            config,
            "physics",
            "milliHeatPerFlux",
            50,
            "How much milli-Heat is required to generate one RF",
            1,
            1_000_000_000
    )
}