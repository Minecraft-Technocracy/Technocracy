package net.cydhra.technocracy.foundation.content.tileentities

import net.cydhra.technocracy.foundation.util.structures.BlockInfo
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.IChunkProvider


/**
 * Replaces a block and stores the original state of it
 */
open class TileBlockWrapper() : AggregatableTileEntity() {

    constructor(info: BlockInfo) : this() {
        block = info
    }

    open fun getWorld(world: World): World {
        return world
    }

    var block: BlockInfo? = null

    fun restoreOriginalBlock() {
        val block = block ?: return

        world.setBlockState(this.pos, block.state, 0);

        if (block.nbt != null) {
            block.nbt.setInteger("x", this.pos.x)
            block.nbt.setInteger("z", this.pos.y)
            block.nbt.setInteger("y", this.pos.z)

            world.getTileEntity(pos)?.readFromNBT(block.nbt)
        }

        markDirty()
        markRenderUpdate()
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        val name = compound.getString("block")
        val meta = compound.getInteger("meta")
        var blockNBT: NBTTagCompound? = null

        if (compound.hasKey("nbt")) {
            blockNBT = compound.getCompoundTag("nbt")
        }

        block = BlockInfo(BlockPos.ORIGIN, Block.getBlockFromName(name)!!, meta, blockNBT)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val block = block ?: return super.writeToNBT(compound)

        compound.apply {
            setString("block", block.block.registryName.toString())
            setInteger("meta", block.meta)

            if (block.nbt != null) {
                setTag("nbt", block.nbt)
            }
        }

        return super.writeToNBT(compound)
    }
}

/**
 * We need another block just for tiles so we can use the vbo for blocks and special renderer for tiles
 */
class TileTileWrapper : TileBlockWrapper() {
    val wrappedTile by lazy {
        val block_ = block!!.block

        if (block_ is ITileEntityProvider) {
            return@lazy block_.createNewTileEntity(world, block!!.meta)
        }
        block_.createTileEntity(world, block!!.state)
    }

    override fun getWorld(world: World): World {
        return CustomTEWorld(world, pos, wrappedTile)
    }

    class CustomTEWorld(val parent: World, val blockPos: BlockPos, val tileEntity: TileEntity?) :
        World(parent.saveHandler, parent.worldInfo, parent.provider, parent.profiler, parent.isRemote) {
        override fun createChunkProvider(): IChunkProvider {
            return parent.chunkProvider
        }

        override fun isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean {
            return allowEmpty || !parent.chunkProvider.provideChunk(x, z).isEmpty
        }

        override fun getChunkFromChunkCoords(chunkX: Int, chunkZ: Int): Chunk {
            return parent.getChunkFromChunkCoords(chunkX, chunkZ)
        }

        override fun getTileEntity(pos: BlockPos): TileEntity? {
            if (blockPos == pos) {
                return tileEntity
            }
            return super.getTileEntity(pos)
        }
    }
}