package net.cydhra.technocracy.astronautics.dyson

import net.minecraft.entity.EnumCreatureType
import net.minecraft.entity.monster.EntityPolarBear
import net.minecraft.util.math.MathHelper
import net.minecraft.world.biome.Biome
import net.minecraftforge.fml.common.registry.ForgeRegistries

object DysonSphereController {

    val originalTemperatureCache = mutableMapOf<Biome, Float>()

    fun initialize() {
        @Suppress("DEPRECATION") // i know they'll change it, but that wont change this code
        ForgeRegistries.BIOMES.values.forEach {
            originalTemperatureCache[it] = it.temperature
        }
    }

    //amount between 0 and 1, 1 is fully build sphere
    var sphereAmount = 0f
        set(value) {
            @Suppress("DEPRECATION") // i know they'll change it, but that wont change this code
            ForgeRegistries.BIOMES.values.forEach { biome ->
                biome.temperature = originalTemperatureCache[biome]!! - 2 * MathHelper.clamp(value, 0f, 1f)

                if (value > 0.3) {
                    biome.enableRain = true

                    if (biome.rainfall < 0.3) {
                        biome.rainfall = 0.3f
                    }
                }

                if (value > 0.4) {
                    biome.decorator.grassPerChunk = 0
                    biome.enableSnow = true
                }

                if (value > 0.6) {
                    if (!biome.getSpawnableList(EnumCreatureType.CREATURE).any { it.entityClass == EntityPolarBear::class.java }
                            && biome.temperature < 0.2) {
                        biome.getSpawnableList(EnumCreatureType.CREATURE).add(Biome.SpawnListEntry(EntityPolarBear::class.java, 4, 2, 6))
                    }
                }
            }

            field = MathHelper.clamp(value, 0f, 1f)
        }
}