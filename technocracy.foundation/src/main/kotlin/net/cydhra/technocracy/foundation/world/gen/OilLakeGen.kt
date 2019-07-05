package net.cydhra.technocracy.foundation.world.gen

import net.cydhra.technocracy.foundation.blocks.general.oilBlock
import net.cydhra.technocracy.foundation.blocks.general.oilStone
import net.cydhra.technocracy.foundation.util.WeightedBlock
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*
import net.minecraft.world.biome.BiomeDesert
import net.minecraft.world.biome.BiomeMesa
import net.minecraftforge.fluids.IFluidBlock


class OilLakeGen : IWorldGenerator {

    val genNormal: WorldGenAdvLakes = WorldGenAdvLakes(mutableListOf(WeightedBlock(oilBlock)))
    val genOilField: WorldGenAdvLakes = WorldGenAdvLakes(mutableListOf(WeightedBlock(oilBlock)))

    init {
        genNormal.outlineBlock = mutableListOf(WeightedBlock(oilStone))
        genNormal.height = 5
        genNormal.width = 12

        genOilField.outlineBlock = mutableListOf(WeightedBlock(oilStone))
        genOilField.height = 15
        genOilField.width = 45
    }


    fun canGenerateInBiome(world: World, blockX: Int, blockZ: Int, random: Random): Boolean {
        if (world.provider.isSurfaceWorld) {
            val bigger = generateHugeOilFields(world, blockX, blockZ)

            val rnd = random.nextInt(1000)

            if (!bigger) {
                return rnd < 25
            } else {
                return rnd < 40
            }
        }

        return false
    }

    fun generateHugeOilFields(world: World, blockX: Int, blockZ: Int): Boolean {
        val biome = world.getBiome(BlockPos(blockX * 16, 0, blockZ * 16))
        return net.minecraftforge.common.BiomeManager.oceanBiomes.contains(biome) || biome is BiomeMesa
    }

    fun getSurfaceBlockY(world: World, x: Int, z: Int): Int {
        var y = world.getChunkFromBlockCoords(BlockPos(x, 0, z)).topFilledSegment + 16

        var pos: BlockPos
        var state: IBlockState
        var block: Block
        do {
            if (--y < 0) {
                break
            }
            pos = BlockPos(x, y, z)
            state = world.getBlockState(pos)
            block = state.block
        } while (block.isAir(state, world, pos) ||
                block.isReplaceable(world, pos) ||
                block.isWood(world, pos) ||
                block.isFoliage(world, pos) ||
                block.isLeaves(state, world, pos) ||
                block.canBeReplacedByLeaves(state, world, pos) ||
                block is IFluidBlock)// @formatter:off
        // @formatter:on
        return y
    }

    override fun generate(random: Random, blockX: Int, blockZ: Int, world: World, chunkGenerator: IChunkGenerator?, chunkProvider: IChunkProvider?) {
        if (!canGenerateInBiome(world, blockX, blockZ, random))
            return

        var posY = getSurfaceBlockY(world, blockX * 16, blockZ * 16) - 15 - 8

        while (posY > 40) {
            posY -= random.nextInt(16)
        }

        if (posY > 0) {
            if (generateHugeOilFields(world, blockX, blockZ)) {
                genOilField.generate(world, random, BlockPos(blockX * 16 + random.nextInt(16), posY, blockZ * 16 + random.nextInt(16)))
            } else {
                genNormal.generate(world, random, BlockPos(blockX * 16 + random.nextInt(16), posY, blockZ * 16 + random.nextInt(16)))
            }
        }
    }
}