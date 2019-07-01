package net.cydhra.technocracy.foundation.world.gen

import net.cydhra.technocracy.foundation.util.ConstantProvider
import net.cydhra.technocracy.foundation.util.INumberProvider
import net.cydhra.technocracy.foundation.util.WeightedBlock
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import net.minecraft.block.Block
import net.minecraft.block.state.pattern.BlockMatcher
import net.minecraft.item.ItemStack
import net.minecraft.util.WeightedRandom
import java.util.ArrayList

class WorldGenAdvLakes(resource: List<WeightedBlock>, block: List<WeightedBlock>?) {
    private val GAP_BLOCK = Arrays.asList(WeightedBlock(Blocks.AIR, 0))
    private var cluster: List<WeightedBlock>
    private var genBlock: Array<WeightedBlock>?
    private var outlineBlock: List<WeightedBlock>? = null
    private var gapBlock = GAP_BLOCK
    private var solidOutline = false
    private var totalOutline = false
    private var width: INumberProvider? = null
    private var height: INumberProvider? = null

    init {
        cluster = resource
        if (block == null) {
            genBlock = null
        } else {
            genBlock = block.toTypedArray()
        }
        this.setWidth(16)
        this.setHeight(9)
    }

    fun generate(world: World, rand: Random, pos: BlockPos): Boolean {
        val data = INumberProvider.DataHolder(pos)

        val width = this.width!!.intValue(world, rand, data)
        val height = this.height!!.intValue(world, rand, data)

        val offX = (rand.nextInt(10) + 5) / 100.0
        val offZ = (rand.nextInt(10) + 5) / 100.0

        val xSize = width.toDouble() - rand.nextDouble() * rand.nextInt((Math.max((width * offX).toInt(), 1)))
        val zSize = width.toDouble() - rand.nextDouble() * rand.nextInt((Math.max((width * offZ).toInt(), 1)))
        val ySize = height - rand.nextDouble() * rand.nextInt(width / 6)

        //val spawnBlock = Array(width) { Array(height) { BooleanArray(width) } }
        // val outline = Array(width + 1) { Array(height + 1) { BooleanArray(width + 1) } }

        val xCenter = width / 2
        val zCenter = width / 2
        val yCenter = height / 2

        val halfx = xSize.toInt() / 2
        val halfy = ySize.toInt() / 2
        val halfz = zSize.toInt() / 2

        val distx = (rand.nextInt(4) + 2)
        val distz = (rand.nextInt(4) + 2)

        val liquidheight = (0.4 + (rand.nextInt(5) / 10.0))

        for (x in xCenter - halfx..xCenter + halfx) {
            for (z in zCenter - halfz..zCenter + halfz) {
                for (y in yCenter - halfy..yCenter + halfy) {
                    val xDist = (x - xCenter) / (xSize / distx.toDouble())
                    val zDist = (z - zCenter) / (zSize / distz.toDouble())
                    val yDist = (y - yCenter) / (ySize / 2.0)
                    val distXZInner = xDist * xDist + zDist * zDist
                    val distXYZInner = xDist * xDist + yDist * yDist + zDist * zDist


                    var spawn = false

                    if (y < height * (0.2 + rand.nextInt(3) / 10) && y > height * 0.3) {
                        //Oil
                        if (distXZInner < 1)
                            spawn = true
                        //spawnBlock[x][y][z] = true
                    } else {
                        if (distXYZInner < 1 - 0.25 * rand.nextDouble()) {//noise at the top and bottom
                            spawn = true
                            //spawnBlock[x][y][z] = true
                        }
                    }
                    if (spawn) {
                        if (y < height * liquidheight) {
                            generateBlock(world, rand, xCenter + pos.x + x - xSize.toInt() / 2, yCenter.toInt() + pos.y + y - ySize.toInt() / 2, zCenter.toInt() + pos.z + z - zSize.toInt() / 2, cluster)
                        } else {
                            generateBlock(world, rand, xCenter + pos.x + x - xSize.toInt() / 2, yCenter.toInt() + pos.y + y - ySize.toInt() / 2, zCenter.toInt() + pos.z + z - zSize.toInt() / 2, gapBlock)
                        }
                    }
                }
            }
        }
        /*

        for (x in 0 until width - 1) {
            for (z in 0 until width - 1) {
                for (y in 0 until height - 1) {
                    val flag = spawnBlock[x][y][z] && (x >= width || spawnBlock[x + 1][y][z]) && (x < 1 || spawnBlock[x - 1][y][z]) && (z >= width || spawnBlock[x][y][z + 1]) && (z < 1 || spawnBlock[x][y][z - 1]) && (y >= height || spawnBlock[x][y + 1][z]) && (y < 1 || spawnBlock[x][y - 1][z])

                    if (flag) {
                        if (y < height * 0.7) {
                            generateBlock(world, rand, xCenter + pos.x + x - xSize.toInt() / 2, yCenter.toInt() + pos.y + y - ySize.toInt() / 2, zCenter.toInt() + pos.z + z - zSize.toInt() / 2, cluster)
                        } else {
                            generateBlock(world, rand, xCenter + pos.x + x - xSize.toInt() / 2, yCenter.toInt() + pos.y + y - ySize.toInt() / 2, zCenter.toInt() + pos.z + z - zSize.toInt() / 2, gapBlock)
                        }
                    }
                }
            }
        }

        if (outlineBlock != null) {
            for (x in 0 until width - 1) {
                for (z in 0 until width - 1) {
                    for (y in 0 until height - 1) {


                        val flag = outline[x][y][z] && !spawnBlock[x][y][z]
                        if (flag) {
                            if (y < height * 0.7) {
                                generateBlock(world, rand, xCenter + pos.x + x - xSize.toInt() / 2, yCenter.toInt() + pos.y + y - ySize.toInt() / 2, zCenter.toInt() + pos.z + z - zSize.toInt() / 2, outlineBlock!!)
                            } else {
                                generateBlock(world, rand, xCenter + pos.x + x - xSize.toInt() / 2, yCenter.toInt() + pos.y + y - ySize.toInt() / 2, zCenter.toInt() + pos.z + z - zSize.toInt() / 2, gapBlock)
                            }
                        }
                        //val flag = !spawnBlock[x][y][z] && ((x <= width && spawnBlock[x + 1][y][z]) || (x > 1 && spawnBlock[x - 1][y][z]) || (z <= width && spawnBlock[x][y][z + 1]) || (z > 1 && spawnBlock[x][y][z - 1]) || (y <= height && spawnBlock[x][y + 1][z]) || (y > 1 && spawnBlock[x][y - 1][z]))

                        /*val flag = !spawnBlock[x][y][z] && (x < width && spawnBlock[x + 1][y][z] || x > 0 && spawnBlock[x - 1][y][z] || z < width && spawnBlock[x][y][z + 1] || z > 0 && spawnBlock[x][y][z + 1] || y < height && spawnBlock[x][y + 1][z] || y > 0 && spawnBlock[x][y - 1][z])


                        if (flag) {
                            if (y < height * 0.7) {
                                generateBlock(world, rand, xCenter + pos.x + x - xSize.toInt() / 2, yCenter.toInt() + pos.y + y - ySize.toInt() / 2, zCenter.toInt() + pos.z + z - zSize.toInt() / 2, outlineBlock!!)
                            } else {
                                generateBlock(world, rand, xCenter + pos.x + x - xSize.toInt() / 2, yCenter.toInt() + pos.y + y - ySize.toInt() / 2, zCenter.toInt() + pos.z + z - zSize.toInt() / 2, gapBlock)
                            }
                        }*/
                    }
                }
            }
        }

        /*var i = 0
        val e = rand.nextInt(4) + 4
        while (i < e) {
            val xSize = rand.nextDouble() * 6.0 + 3.0
            val ySize = rand.nextDouble() * 4.0 + 2.0
            val zSize = rand.nextDouble() * 6.0 + 3.0
            val xCenter = rand.nextDouble() * (width.toDouble() - xSize - 2.0) + 1.0 + xSize / 2.0
            val yCenter = rand.nextDouble() * (height.toDouble() - ySize - 4.0) + 2.0 + ySize / 2.0
            val zCenter = rand.nextDouble() * (width.toDouble() - zSize - 2.0) + 1.0 + zSize / 2.0

            for (x in 1 until W) {
                for (z in 1 until W) {
                    for (y in 1 until H) {
                        val xDist = (x - xCenter) / (xSize / 2.0)
                        val yDist = (y - yCenter) / (ySize / 2.0)
                        val zDist = (z - zCenter) / (zSize / 2.0)
                        val dist = xDist * xDist + yDist * yDist + zDist * zDist

                        if (dist < 1.0 || true) {
                            spawnBlock[(x * width + z) * height + y] = true
                        }
                    }
                }
            }
            ++i
        }

        var x: Int
        var y: Int
        var z: Int

        x = 0
        while (x < width) {
            z = 0
            while (z < width) {
                y = 0
                while (y < height) {
                    val flag = spawnBlock[(x * width + z) * height + y] || x < W && spawnBlock[((x + 1) * width + z) * height + y] || x > 0 && spawnBlock[((x - 1) * width + z) * height + y] || z < W && spawnBlock[(x * width + (z + 1)) * height + y] || z > 0 && spawnBlock[(x * width + (z - 1)) * height + y] || y < H && spawnBlock[(x * width + z) * height + (y + 1)] || y > 0 && spawnBlock[(x * width + z) * height + (y - 1)]

                    if (flag) {
                        if (y >= heightOff) {
                            val material = world.getBlockState(BlockPos(xStart + x, yStart + y, zStart + z)).getMaterial()
                            if (material.isLiquid()) {
                                return false
                            }
                        } else {
                            if (!canGenerateInBlock(world, xStart + x, yStart + y, zStart + z, genBlock)) {
                                return false
                            }
                        }
                    }
                    ++y
                }
                ++z
            }
            ++x
        }

        x = 0
        while (x < width) {
            z = 0
            while (z < width) {
                y = 0
                while (y < height) {
                    if (spawnBlock[(x * width + z) * height + y]) {
                        if (y < heightOff) {
                            generateBlock(world, rand, xStart + x, yStart + y, zStart + z, genBlock, cluster)
                        } else if (canGenerateInBlock(world, xStart + x, yStart + y, zStart + z, genBlock)) {
                            generateBlock(world, rand, xStart + x, yStart + y, zStart + z, gapBlock)
                        }
                    }
                    ++y
                }
                ++z
            }
            ++x
        }

        x = 0
        while (x < width) {
            z = 0
            while (z < width) {
                y = 0
                while (y < height) {
                    if (spawnBlock[(x * width + z) * height + y] && world.getBlockState(BlockPos(xStart + x, yStart + y - 1, zStart + z)).getBlock().equals(Blocks.DIRT) && world.getLightFor(EnumSkyBlock.SKY, BlockPos(xStart + x, yStart + y, zStart + z)) > 0) {
                        val bgb = world.getBiome(BlockPos(xStart + x, 0, zStart + z))
                        world.setBlockState(BlockPos(xStart + x, yStart + y - 1, zStart + z), bgb.topBlock, 2)
                    }
                    ++y
                }
                ++z
            }
            ++x
        }

        if (outlineBlock != null) {
            x = 0
            while (x < width) {
                z = 0
                while (z < width) {
                    y = 0
                    while (y < height) {
                        val flag = !spawnBlock[(x * width + z) * height + y] && (x < W && spawnBlock[((x + 1) * width + z) * height + y] || x > 0 && spawnBlock[((x - 1) * width + z) * height + y] || z < W && spawnBlock[(x * width + (z + 1)) * height + y] || z > 0 && spawnBlock[(x * width + (z - 1)) * height + y] || y < H && spawnBlock[(x * width + z) * height + (y + 1)] || y > 0 && spawnBlock[(x * width + z) * height + (y - 1)])

                        if (flag && (solidOutline or (y < heightOff) || rand.nextInt(2) !== 0) && (totalOutline || world.getBlockState(BlockPos(xStart + x, yStart + y, zStart + z)).getMaterial().isSolid())) {
                            generateBlock(world, rand, xStart + x, yStart + y, zStart + z, outlineBlock!!)
                        }
                        ++y
                    }
                    ++z
                }
                ++x
            }
        }

        return true*/*/

        return true
    }

    fun setWidth(width: Int): WorldGenAdvLakes {

        this.width = ConstantProvider(width)
        return this
    }

    fun setWidth(width: INumberProvider): WorldGenAdvLakes {

        this.width = width
        return this
    }

    fun setHeight(height: Int): WorldGenAdvLakes {

        this.height = ConstantProvider(height)
        return this
    }

    fun setHeight(height: INumberProvider): WorldGenAdvLakes {

        this.height = height
        return this
    }

    fun setSolidOutline(outline: Boolean): WorldGenAdvLakes {

        this.solidOutline = outline
        return this
    }

    fun setTotalOutline(outline: Boolean): WorldGenAdvLakes {

        this.totalOutline = outline
        return this
    }

    fun setOutlineBlock(blocks: List<WeightedBlock>): WorldGenAdvLakes {

        this.outlineBlock = blocks
        return this
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