package net.cydhra.technocracy.foundation.content.tileentities.wrapper

import net.cydhra.technocracy.foundation.content.events.StructureDisbandEvent
import net.cydhra.technocracy.foundation.content.tileentities.AggregatableTileEntity
import net.cydhra.technocracy.foundation.util.structures.BlockInfo
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*


/**
 * Replaces a block and stores the original state of it
 */
open class BlockWrapperTileEntity : AggregatableTileEntity() {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun disbandStructure(event: StructureDisbandEvent) {
        if (event.structureId == structureUUID) {
            structureUUID = null
            restoreOriginalBlock()
        }
    }

    open fun getWorld(world: World): World {
        return world
    }

    open fun getAccess(access: IBlockAccess): IBlockAccess {
        return access
    }

    var structureUUID: UUID? = null

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

        if (compound.hasKey("structure")) {
            structureUUID = compound.getUniqueId("structure")
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

            if (structureUUID != null)
                setUniqueId("structure", structureUUID!!)
        }

        return super.writeToNBT(compound)
    }
}

/**
 * We need another block just for tiles so we can use the vbo for blocks and special renderer for tiles
 */
class TileWrapperTileEntity : BlockWrapperTileEntity() {
    var internalTile: TileEntity? = null

    fun getWrappedTile(): TileEntity? {
        val world = world ?: return null

        if (internalTile == null) {
            val block = block

            internalTile = block?.block?.createTileEntity(CustomTEWorld(this, world, pos, null), block.state)
                ?: return null

            if (block.nbt != null)
                internalTile?.deserializeNBT(block.nbt)

            internalTile?.world = getWorld(world)
        }
        return internalTile
    }

    override fun getWorld(world: World): World {
        return CustomTEWorld(this, world, pos, getWrappedTile())
    }

    override fun getAccess(access: IBlockAccess): IBlockAccess {
        return CustomTEAccess(this, access, pos, getWrappedTile())
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val block = block ?: return super.writeToNBT(compound)

        this.block = BlockInfo(block.pos, block.block, block.meta, internalTile?.serializeNBT() ?: block.nbt)

        return super.writeToNBT(compound)
    }

    private class CustomTEAccess(
        val parentTile: BlockWrapperTileEntity,
        val parent: IBlockAccess,
        val blockPos: BlockPos,
        val tileEntity: TileEntity?
    ) :
        IBlockAccess by parent {
        override fun getTileEntity(pos: BlockPos): TileEntity? {
            if (blockPos == pos) {
                return tileEntity
            }

            val other = parent.getTileEntity(pos)
            if (other is TileWrapperTileEntity) {
                other.getWrappedTile()
            }
            return other
        }

        override fun getBlockState(pos: BlockPos): IBlockState {
            val tile = parent.getTileEntity(pos)
            if (tile is BlockWrapperTileEntity) {
                return tile.block?.state ?: parent.getBlockState(pos)
            }
            return parent.getBlockState(pos)
        }
    }

    private class CustomTEWorld(
        val parentTile: BlockWrapperTileEntity,
        val parent: World,
        val blockPos: BlockPos,
        val tileEntity: TileEntity?
    ) :
        World(parent.saveHandler, parent.worldInfo, parent.provider, parent.profiler, parent.isRemote) {

        override fun markChunkDirty(pos: BlockPos, unusedTileEntity: TileEntity) {
            if (pos == blockPos && unusedTileEntity == tileEntity) {
                parentTile.markDirty()
            } else {
                super.markChunkDirty(pos, unusedTileEntity)
            }
        }

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

            val other = super.getTileEntity(pos)

            if (other is TileWrapperTileEntity) {
                other.getWrappedTile()
            }
            return other
        }

        override fun getBlockState(pos: BlockPos): IBlockState {
            val tile = super.getTileEntity(pos)
            if (tile is BlockWrapperTileEntity) {
                return tile.block?.state ?: super.getBlockState(pos)
            }
            return super.getBlockState(pos)
        }
    }
}