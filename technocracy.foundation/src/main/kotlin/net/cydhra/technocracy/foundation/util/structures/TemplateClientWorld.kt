package net.cydhra.technocracy.foundation.util.structures

import net.cydhra.technocracy.foundation.util.structures.BlockInfo
import net.minecraft.block.state.IBlockState
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class TemplateClientWorld(val client: WorldClient, val blocks: MutableList<BlockInfo>, val currentPosForLight: BlockPos) : World(client.saveHandler, client.worldInfo, client.provider, client.profiler, client.isRemote) {
    override fun createChunkProvider(): IChunkProvider {
        return client.chunkProvider
    }

    @SideOnly(Side.CLIENT)
    override fun getLightFromNeighborsFor(type: EnumSkyBlock, pos: BlockPos): Int {
        return client.getLightFromNeighborsFor(type, pos.add(currentPosForLight))
    }

    override fun getLightFor(type: EnumSkyBlock, pos: BlockPos): Int {
        return client.getLightFor(type, pos.add(currentPosForLight))
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
                return info.state
        }
        return Blocks.AIR.defaultState
    }
}