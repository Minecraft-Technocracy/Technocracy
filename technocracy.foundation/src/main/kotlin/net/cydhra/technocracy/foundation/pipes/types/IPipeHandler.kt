package net.cydhra.technocracy.foundation.pipes.types

import net.cydhra.technocracy.foundation.pipes.FilteredPipeTypeGraph
import net.cydhra.technocracy.foundation.pipes.WrappedBlockPos
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World


interface IPipeHandler {
    /**
     * @param world the world instance of the pipe
     * @param currentNode the node of the pipe from the [graph]
     * @param extractionPipe the pipe that extracts content from [extractionTileEntity]
     * @param extractionTileEntity the target TileEntity that gets stuff extracted
     * @param extractionFacing the facing from the [extractionPipe] to the [extractionTileEntity]
     * @param graph the graph that filters its edges
     * @return timeout for the pipe
     */
    fun handle(world: World, currentNode: WrappedBlockPos, extractionPipe: TileEntityPipe, extractionTileEntity:
    TileEntity,
            extractionFacing: EnumFacing, graph:
    FilteredPipeTypeGraph): Int

    val type: PipeType
}