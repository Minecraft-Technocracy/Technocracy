package net.cydhra.technocracy.foundation.world.gen

import net.cydhra.technocracy.foundation.blocks.general.oilSandBlock
import net.cydhra.technocracy.foundation.util.WeightedBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockSand
import net.minecraft.block.BlockSandStone
import net.minecraft.block.state.IBlockState
import net.minecraft.util.WeightedRandom
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*


class OilSandGen : IWorldGenerator {
    fun canGenerateInBiome(world: World, blockX: Int, blockZ: Int, random: Random): Boolean {
        if (world.provider.isSurfaceWorld) {
            val rnd = random.nextInt(1000)
            return rnd < 22
        }

        return false
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

        var posY = getSurfaceBlockY(world, blockX * 16, blockZ * 16)

        if (posY > 0) {
            gen(world, random, BlockPos(blockX * 16 + random.nextInt(16), posY, blockZ * 16 + random.nextInt(16)))
        }
    }

    fun gen(world: World, rand: Random, pos: BlockPos) {
        val width = 15

        val offX = (rand.nextInt(10) + 5) / 100.0
        val offZ = (rand.nextInt(10) + 5) / 100.0

        val xSize = width.toDouble() - rand.nextDouble() * rand.nextInt((Math.max((width * offX).toInt(), 1)))
        val zSize = width.toDouble() - rand.nextDouble() * rand.nextInt((Math.max((width * offZ).toInt(), 1)))

        val xCenter = width / 2
        val zCenter = width / 2

        val halfx = xSize.toInt() / 2
        val halfz = zSize.toInt() / 2

        val distx = (rand.nextInt(4) + 2)
        val distz = (rand.nextInt(4) + 2)

        val depth = rand.nextInt(4) + 1

        for (x in xCenter - halfx..xCenter + halfx) {
            for (z in zCenter - halfz..zCenter + halfz) {
                for (y in -depth..10) {
                    val xDist = (x - xCenter) / (xSize / distx.toDouble())
                    val zDist = (z - zCenter) / (zSize / distz.toDouble())
                    val distXZInner = xDist * xDist + zDist * zDist

                    if (distXZInner < 1 - 0.25 * rand.nextDouble()) {//noise at the top and bottom
                        generateBlock(world, rand, xCenter + pos.x + x, pos.y + y, zCenter + pos.z + z, arrayListOf(WeightedBlock(oilSandBlock)))
                    }
                }
            }
        }
    }

    fun generateBlock(world: World, rand: Random, x: Int, y: Int, z: Int, o: List<WeightedBlock>): Boolean {
        val pos = BlockPos(x, y, z)
        val currstate = world.getBlockState(pos)
        if (currstate.block is BlockSand || currstate.block is BlockSandStone) {
            return setBlock(world, pos, selectBlock(rand, o))
        }
        return false
    }

    fun setBlock(world: World, pos: BlockPos, ore: WeightedBlock?): Boolean {
        if (ore != null && world.setBlockState(pos, ore.getState(), 2 or 16)) {
            return true
        }
        return false
    }

    fun selectBlock(rand: Random, o: List<WeightedBlock>): WeightedBlock? {
        val size = o.size
        if (size == 0) {
            return null
        }
        return if (size > 1) {
            WeightedRandom.getRandomItem(rand, o)
        } else o[0]
    }
}