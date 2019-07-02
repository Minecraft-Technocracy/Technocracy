package net.cydhra.technocracy.foundation.world.gen

import net.cydhra.technocracy.foundation.util.WeightedBlock
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import net.minecraft.util.WeightedRandom

class WorldGenAdvLakes(var liquids: List<WeightedBlock>) {
    var gapBlock = Arrays.asList(WeightedBlock(Blocks.AIR, 0))
    var outlineBlock: List<WeightedBlock>? = null
    var width = 16
    var height = 8

    fun generate(world: World, rand: Random, pos: BlockPos): Boolean {
        val width = this.width
        val height = this.height

        val offX = (rand.nextInt(10) + 5) / 100.0
        val offZ = (rand.nextInt(10) + 5) / 100.0

        val xSize = width.toDouble() - rand.nextDouble() * rand.nextInt((Math.max((width * offX).toInt(), 1)))
        val zSize = width.toDouble() - rand.nextDouble() * rand.nextInt((Math.max((width * offZ).toInt(), 1)))
        val ySize = height - rand.nextDouble() * rand.nextInt(width / 6)

        val xCenter = width / 2
        val zCenter = width / 2
        val yCenter = height / 2

        val halfx = xSize.toInt() / 2
        val halfy = ySize.toInt() / 2
        val halfz = zSize.toInt() / 2

        val distx = (rand.nextInt(4) + 2)
        val distz = (rand.nextInt(4) + 2)

        val genrateOverhang = rand.nextBoolean()

        val liquidheight = (0.4 + (rand.nextInt(5) / 10.0))

        for (x in xCenter - halfx..xCenter + halfx) {
            for (z in zCenter - halfz..zCenter + halfz) {
                for (y in yCenter - halfy - 1..yCenter + halfy) {
                    val xDist = (x - xCenter) / (xSize / distx.toDouble())
                    val zDist = (z - zCenter) / (zSize / distz.toDouble())
                    val yDist = (y - yCenter) / (ySize / 2.0)
                    val distXZInner = xDist * xDist + zDist * zDist
                    val distXYZInner = xDist * xDist + yDist * yDist + zDist * zDist


                    val xDist2 = (x - xCenter) / ((xSize / distx.toDouble()) + 1)
                    val zDist2 = (z - zCenter) / ((zSize / distz.toDouble()) + 1)
                    val yDist2 = (y - yCenter) / ((ySize / 2.0) + 1)
                    val distXZInner2 = xDist2 * xDist2 + zDist2 * zDist2
                    val distXYZInner2 = xDist2 * xDist2 + yDist2 * yDist2 + zDist2 * zDist2

                    var spawn = false
                    var spawnOuter = false

                    if (y < height * (0.2 + rand.nextInt(3) / 10) && y > height * 0.3) {
                        //Liquid
                        if (distXZInner < 1) {
                            spawn = true
                        }
                    } else {
                        if (distXYZInner < 1 - 0.25 * rand.nextDouble()) {//noise at the top and bottom
                            spawn = true
                        }
                    }

                    if (spawn) {
                        if (y < height * liquidheight) {
                            generateBlock(world, rand, xCenter + pos.x + x, yCenter + pos.y + y, zCenter + pos.z + z, liquids)
                        } else {
                            generateBlock(world, rand, xCenter + pos.x + x, yCenter + pos.y + y, zCenter + pos.z + z, gapBlock)
                        }
                    }

                    if (outlineBlock != null) {
                        if (y < height * 0.4 && y > height * 0.3) {
                            //Liquid
                            if (distXZInner2 <= 1 - 0.25 * rand.nextDouble()) {
                                spawnOuter = true
                            }
                        } else {
                            if (distXYZInner2 <= 1 - 0.25 * rand.nextDouble()) {
                                spawnOuter = true
                            }
                        }

                        if (!spawn && spawnOuter) {
                            if (y < height * liquidheight) {
                                if (rand.nextInt(10) < 7)
                                    generateBlock(world, rand, xCenter + pos.x + x, yCenter + pos.y + y, zCenter + pos.z + z, outlineBlock!!)
                            } else {
                                if (genrateOverhang)
                                    generateBlock(world, rand, xCenter + pos.x + x, yCenter + pos.y + y, zCenter + pos.z + z, gapBlock)
                            }
                        }
                    }
                }
            }
        }

        return true
    }

    fun generateBlock(world: World, rand: Random, x: Int, y: Int, z: Int, o: List<WeightedBlock>): Boolean {
        return setBlock(world, BlockPos(x, y, z), selectBlock(rand, o))
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