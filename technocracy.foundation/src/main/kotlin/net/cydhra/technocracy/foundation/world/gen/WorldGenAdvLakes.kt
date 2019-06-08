package net.cydhra.technocracy.foundation.world.gen

import net.cydhra.technocracy.foundation.util.ConstantProvider
import net.cydhra.technocracy.foundation.util.INumberProvider
import net.cydhra.technocracy.foundation.util.WeightedBlock
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.EnumSkyBlock
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
        var xStart = pos.getX()
        var yStart = pos.getY()
        var zStart = pos.getZ()

        val data = INumberProvider.DataHolder(pos)

        val width = this.width!!.intValue(world, rand, data)
        val height = this.height!!.intValue(world, rand, data)

        val widthOff = width / 2
        var heightOff = height / 2 + 1

        xStart -= widthOff
        zStart -= widthOff

        yStart -= widthOff

        while (yStart > heightOff && world.isAirBlock(BlockPos(xStart, yStart, zStart))) {
            --yStart
        }
        --heightOff
        if (yStart <= heightOff) {
            return false
        }

        yStart -= heightOff
        val spawnBlock = BooleanArray(width * width * height)

        val W = width - 1
        val H = height - 1

        var i = 0
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

                        if (dist < 1.0) {
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

    fun setGapBlock(blocks: List<WeightedBlock>): WorldGenAdvLakes {

        this.gapBlock = blocks
        return this
    }

    fun fabricateList(resource: WeightedBlock): List<WeightedBlock> {

        val list = ArrayList<WeightedBlock>()
        list.add(resource)
        return list
    }

    fun fabricateList(resource: Block): List<WeightedBlock> {

        val list = ArrayList<WeightedBlock>()
        list.add(WeightedBlock(ItemStack(resource, 1, 0)))
        return list
    }

    fun canGenerateInBlock(world: World, x: Int, y: Int, z: Int, mat: Array<WeightedBlock>?): Boolean {

        return true//canGenerateInBlock(world, BlockPos(x, y, z), mat)
    }

    fun canGenerateInBlock(world: World, pos: BlockPos, mat: Array<WeightedBlock>?): Boolean {

        if (mat == null || mat.size == 0) {
            return true
        }

        val state = world.getBlockState(pos)
        var j = 0
        val e = mat.size
        while (j < e) {
            val genBlock = mat[j]
            if ((-1 == genBlock.metadata || genBlock.metadata == state.block.getMetaFromState(state)) && (state.block.isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(genBlock.block)) || state.block.isAssociatedBlock(genBlock.block))) {
                return true
            }
            ++j
        }
        return false
    }

    fun generateBlock(world: World, rand: Random, x: Int, y: Int, z: Int, mat: Array<WeightedBlock>?, o: List<WeightedBlock>): Boolean {

        if (mat == null || mat.size == 0) {
            return generateBlock(world, rand, x, y, z, o)
        }

        return if (canGenerateInBlock(world, x, y, z, mat)) {
            generateBlock(world, rand, x, y, z, o)
        } else false
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