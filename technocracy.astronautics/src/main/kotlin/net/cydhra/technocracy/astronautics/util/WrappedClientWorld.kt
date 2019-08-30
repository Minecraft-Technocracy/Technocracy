package net.cydhra.technocracy.astronautics.util

import net.cydhra.technocracy.foundation.util.structures.BlockInfo
import net.minecraft.block.state.IBlockState
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.IChunkProvider


class WrappedClientWorld(val client: WorldClient, val blocks: List<BlockInfo>) : World(client.saveHandler, client.worldInfo, client.provider, client.profiler, client.isRemote) {

    override fun createChunkProvider(): IChunkProvider {
        return client.chunkProvider
    }

    override fun isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean {
        return allowEmpty || !client.chunkProvider.provideChunk(x, z).isEmpty
    }

    override fun getChunkFromChunkCoords(chunkX: Int, chunkZ: Int): Chunk {
        return client.getChunkFromChunkCoords(chunkX, chunkZ)
    }

    override fun getBlockState(pos: BlockPos): IBlockState {
        for (info in blocks) {
            if (info.pos == pos)
                return info.block.getStateFromMeta(info.meta)
        }
        return Blocks.AIR.defaultState
    }
}