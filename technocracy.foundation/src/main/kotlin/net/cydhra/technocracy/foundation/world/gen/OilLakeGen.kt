package net.cydhra.technocracy.foundation.world.gen

import net.cydhra.technocracy.foundation.blocks.general.oilBlock
import net.cydhra.technocracy.foundation.util.WeightedBlock
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*
import net.minecraft.init.Blocks
import net.minecraftforge.fluids.IFluidBlock


class OilLakeGen : IWorldGenerator {

    val genNormal: WorldGenAdvLakes
    val genOilField: WorldGenAdvLakes

    init {
        genNormal = WorldGenAdvLakes(mutableListOf(WeightedBlock(oilBlock)), mutableListOf(WeightedBlock(oilBlock)))

        genNormal.setTotalOutline(false)
        genNormal.setSolidOutline(true)
        genNormal.setOutlineBlock(mutableListOf(WeightedBlock(Blocks.OBSIDIAN)))

        genNormal.setHeight(5)
        genNormal.setWidth(8)


        genOilField = WorldGenAdvLakes(mutableListOf(WeightedBlock(oilBlock)), mutableListOf(WeightedBlock(oilBlock)))

        genOilField.setTotalOutline(false)
        genOilField.setSolidOutline(true)
        genOilField.setOutlineBlock(mutableListOf(WeightedBlock(Blocks.OBSIDIAN)))

        genOilField.setHeight(15)
        genOilField.setWidth(40)
    }


    fun canGenerateInBiome(world: World, blockX: Int, blockZ: Int, random: Random): Boolean {


        return blockX % 5 == 0 && blockZ % 5 == 0
        //val biome = world.getBiome(BlockPos(blockX, 0, blockZ))
        //return true;//net.minecraftforge.common.BiomeManager.oceanBiomes.contains(biome)
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
            block = state.getBlock()
        } while (block.isAir(state, world, pos) ||
                block.isReplaceable(world, pos) ||
                block.isWood(world, pos) ||
                block.isFoliage(world, pos) ||
                block.isLeaves(state, world, pos) ||
                block.canBeReplacedByLeaves(state, world, pos) ||
                state.block is IFluidBlock)// @formatter:off
        // @formatter:on
        return y
    }

    override fun generate(random: Random, blockX: Int, blockZ: Int, world: World, chunkGenerator: IChunkGenerator?, chunkProvider: IChunkProvider?) {


        if (!canGenerateInBiome(world, blockX, blockZ, random))
            return

        val pos = BlockPos(blockX, 64, blockZ)

        val count = 1;//count.intValue(world, random, INumberProvider.DataHolder(pos))


        var generated = false

        var posY = getSurfaceBlockY(world, blockX * 16, blockZ * 16) - 15 - 8

        while (posY > 64) {
            posY -= random.nextInt(16)
        }

        val i = blockX shr 4
        val j = blockZ shr 4
        random.setSeed((i xor (j shl 4)).toLong() xor world.seed)

        if (posY > 0) {
            //genNormal.generate(world, random, BlockPos(blockX * 16 + random.nextInt(8), posY, blockZ * 16 + random.nextInt(8)))
            genNormal.generate(world, random, BlockPos(blockX * 16 + random.nextInt(8), posY, blockZ * 16 + random.nextInt(8)))
        }

        /*for (i in 0 until count) {
    val x = blockX + random.nextInt(16)
    val z = blockZ + random.nextInt(16)
    if (!canGenerateInBiome(world, x, z, random)) {
        continue
    }

    val water = true

    var y = getSurfaceBlockY(world, x, z)
    l@ do {
        val state = world.getBlockState(BlockPos(x, y, z))
        if (water) {
            if (state.material === Material.WATER) {
                continue
            }
            if (world.getBlockState(BlockPos(x, y + 1, z)).material !== Material.WATER) {
                continue
            }
        } else {
            /*var fluid = Utils.lookupFluidForBlock(state.block)
            if (fluid != null && Arrays.binarySearch(fluidList, fluid!!.getName()) >= 0) {
                continue
            }

            fluid = Utils.lookupFluidForBlock(world.getBlockState(BlockPos(x, y + 1, z)).block)
            if (fluid == null || Arrays.binarySearch(fluidList, fluid!!.getName()) < 0) {
                continue
            }*/
        }
            if (state.block.isReplaceableOreGen(state, world, BlockPos(x, y, z), BlockMatcher.forBlock(oilBlock)) || true) {
                break@l
        }
    } while (y-- > 1)

    if (y > 0) {
        gen.generate(world, random, BlockPos(x, y - 10, z))
    }
}*/
    }
}