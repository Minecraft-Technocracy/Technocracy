package net.cydhra.technocracy.foundation.model.world.api

import com.google.common.base.Predicate
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraft.world.gen.feature.WorldGenMinable
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

/**
 * An instance of this class can be registered in the world generator for ore vein generation
 *
 * @param dimensions an array of dimension ids that can hold this ore
 * @param replacementPredicate a predicate determining all blocks that can be replaced with this ore
 * @param generatedBlock the block that is generated in veins
 * @param maxVeinsPerChunk how many times the generator shall try to place a new vein in a chunk
 * @param amountPerVein how many blocks are generated per vein
 * @param minHeight the minimum height the veins are generated
 * @param maxHeight the maximum height the veins are generated
 */
class OreGenerator(private val dimensions: Array<Int>,
                   private val replacementPredicate: Predicate<IBlockState>,
                   private val generatedBlock: IBlockState, private val maxVeinsPerChunk: Int,
                   private val amountPerVein: Int, private val minHeight: Int,
                   private val maxHeight: Int) : IWorldGenerator {

    init {
        check(minHeight > 0) { "Cannot generate below zero" }
        check(maxHeight < 255) { "Cannot generate above build limit" }
        check(minHeight < maxHeight) { "Minimum generation height is above maximum generation height" }
    }

    companion object {
        private const val CHUNK_SIZE = 16
    }

    override fun generate(rnd: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkGenerator, chunkProvider: IChunkProvider) {
        if (world.provider.dimension in dimensions) {
            tryGenerateVeins(world, rnd, chunkX, chunkZ)
        }
    }

    private fun tryGenerateVeins(world: World, rnd: Random, chunkX: Int, chunkZ: Int) {
        val generator = WorldGenMinable(generatedBlock, amountPerVein, replacementPredicate)
        repeat(maxVeinsPerChunk) {
            val veinX = chunkX * CHUNK_SIZE + rnd.nextInt(CHUNK_SIZE)
            val veinY = rnd.nextInt(maxHeight - minHeight) + minHeight
            val veinZ = chunkZ * CHUNK_SIZE + rnd.nextInt(CHUNK_SIZE)

            generator.generate(world, rnd, BlockPos(veinX, veinY, veinZ))
        }
    }
}