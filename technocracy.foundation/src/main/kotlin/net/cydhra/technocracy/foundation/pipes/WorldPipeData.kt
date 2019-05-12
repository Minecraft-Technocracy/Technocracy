package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTUtil
import net.minecraft.nbt.NBTTagByte
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import kotlin.experimental.and


class WorldPipeData(val network: Network) {
    fun readNBT(world: World, nbt: NBTTagCompound) {
        val worldId = world.provider.dimensionType.getName() + "_" + world.provider.dimension
        if (nbt.hasKey(worldId)) {
            val worldCompound = nbt.getTagList(worldId, 10)
            for (networkCounter in 0 until worldCompound.tagCount()) {
                val networkComponent = worldCompound.get(networkCounter) as NBTTagCompound
                val networkId = networkComponent.getUniqueId("id")

                if (networkComponent.hasKey("vertices")) {
                    val vertices = networkComponent.getTagList("vertices", 10)
                    for (pos in 0 until vertices.tagCount()) {

                        val wrappedPos = vertices.getCompoundTagAt(pos)
                        val readPos = NBTUtil.getPosFromTag(wrappedPos)
                        val wrapped = WrappedBlockPos(readPos)

                        if (wrappedPos.hasKey("io")) {
                            val io = wrappedPos.getCompoundTag("io")

                            for (type in PipeType.values()) {
                                if (io.hasKey(type.name)) {
                                    for(ioEnum in Network.IO.values()) {
                                        val facesEncoded = io.getByte(type.name + "_" + ioEnum.name)

                                        val facesList = mutableSetOf<EnumFacing>()

                                        for (facing in EnumFacing.values()) {
                                            if (facesEncoded.and((1 shl facing.index).toByte()).toInt() != -1) {
                                                facesList.add(facing)
                                            }
                                        }

                                        val map = wrapped.io.getOrPut(type) {
                                            mutableMapOf()
                                        }
                                        map[ioEnum] = facesList
                                    }
                                }
                            }
                        }

                        network.loadNode(wrapped, networkId!!, world)
                    }
                }

                if (networkComponent.hasKey("edges")) {
                    val edges = networkComponent.getCompoundTag("edges")
                    for (type in PipeType.values()) {
                        if (!edges.hasKey(type.name)) continue

                        val edgeList = edges.getTagList(type.name, 10)
                        for (edge in 0 until edgeList.tagCount()) {
                            val vertexes = edgeList.get(edge) as NBTTagCompound
                            val source = NBTUtil.getPosFromTag(vertexes.getCompoundTag("source"))
                            val target = NBTUtil.getPosFromTag(vertexes.getCompoundTag("target"))
                            network.loadEdge(source, target, networkId!!, world, type)
                        }
                    }
                }
            }
        }
    }

    fun writeNBT(): NBTTagCompound {
        val compound = NBTTagCompound()
        network.networks.forEach { mapname, net ->
            val worldCompound = NBTTagList()
            net.forEach { networkkey, blocks ->
                val networkCompound = NBTTagCompound()

                networkCompound.setUniqueId("id", networkkey)

                val vertices = NBTTagList()
                blocks.vertexSet().forEach { pos ->

                    val wrappedBlockPos = NBTUtil.createPosTag(pos.pos)

                    if (pos.hasIO) {
                        val pipeface = NBTTagCompound()

                        pos.io.forEach { pipeType, ioMap ->
                            ioMap.forEach { ioEnum, faces ->
                                var all = 0
                                for (facing in faces) {
                                    all = all or (1 shl facing.index)
                                }

                                pipeface.setTag(pipeType.name + "_" + ioEnum.name, NBTTagByte(all.toByte()))
                            }
                        }

                        wrappedBlockPos.setTag("io", pipeface)
                    }

                    vertices.appendTag(wrappedBlockPos)
                }

                if (vertices.tagCount() != 0) networkCompound.setTag("vertices", vertices)

                val edges = NBTTagCompound()
                for (type in PipeType.values()) {
                    val edgeList = NBTTagList()
                    blocks.edgeSet().filter { it.pipeType == type }.forEach { edge ->
                        val edgeVertices = NBTTagCompound()

                        val src = blocks.getEdgeSource(edge)
                        val target = blocks.getEdgeTarget(edge)

                        edgeVertices.setTag("source", NBTUtil.createPosTag(src.pos))
                        edgeVertices.setTag("target", NBTUtil.createPosTag(target.pos))

                        edgeList.appendTag(edgeVertices)
                    }

                    if (edgeList.tagCount() != 0) edges.setTag(type.name, edgeList)
                }
                networkCompound.setTag("edges", edges)

                worldCompound.appendTag(networkCompound)
            }

            compound.setTag(mapname, worldCompound)

        }

        return compound
    }
}