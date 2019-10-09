package net.cydhra.technocracy.foundation.content.world

import net.cydhra.technocracy.foundation.content.blocks.chrysotileBlock
import net.cydhra.technocracy.foundation.content.blocks.saltBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraft.world.gen.feature.WorldGenMinable
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*


class WorldGenDeco : IWorldGenerator {

    val chrysotileDecorator = WorldGenMinable(chrysotileBlock.defaultState, 33)
    val saltDecorator = WorldGenMinable(saltBlock.defaultState, 15)

    override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkGenerator?, chunkProvider: IChunkProvider?) {
        genStandardOre(world, random, 10, chrysotileDecorator, 20, 100, BlockPos(chunkX * 16, 0, chunkZ * 16))
        genStandardOre(world, random, 8, saltDecorator, 20, 80, BlockPos(chunkX * 16, 0, chunkZ * 16))
    }

    /**
     * Standard ore generation helper. Vanilla uses this to generate most ores.
     */
    @Suppress("NAME_SHADOWING")
    fun genStandardOre(worldIn: World, random: Random, blockCount: Int, generator: WorldGenerator, minHeight: Int, maxHeight: Int, chunkPos: BlockPos) {
        var minHeight = minHeight
        var maxHeight = maxHeight
        if (maxHeight < minHeight) {
            val i = minHeight
            minHeight = maxHeight
            maxHeight = i
        } else if (maxHeight == minHeight) {
            if (minHeight < 255) {
                ++maxHeight
            } else {
                --minHeight
            }
        }

        for (j in 0 until blockCount) {
            val pos = chunkPos.add(random.nextInt(16), random.nextInt(maxHeight - minHeight) + minHeight, random.nextInt(16))
            generator.generate(worldIn, random, pos)
        }
    }
}