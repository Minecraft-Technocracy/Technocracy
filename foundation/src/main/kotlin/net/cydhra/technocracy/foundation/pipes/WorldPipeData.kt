package net.cydhra.technocracy.foundation.pipes

import net.cydhra.technocracy.foundation.pipes.Network.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagLongArray
import net.minecraft.nbt.NBTUtil
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class WorldPipeData(val network: Network) {
    fun readNBT(world: World, nbt: NBTTagCompound) {
        if (nbt.hasKey(world.worldInfo.worldName)) {
            val worldCompound = nbt.getTagList(world.providerName, 9)
            for (networkCounter in 0 until worldCompound.tagCount()) {
                val networkComponent = worldCompound.get(networkCounter) as NBTTagCompound
                val networkId = networkComponent.getUniqueId("id")

                if(networkComponent.hasKey("vertices")) {
                    val vertices = networkComponent.getTagList("vertices", 9)
                    for (pos in 0 until vertices.tagCount()) {
                        network.loadNode(NBTUtil.getPosFromTag(vertices.getCompoundTagAt(pos)),networkId!!, world)
                    }
                }

                if (networkComponent.hasKey("edges")) {
                    val edges = networkComponent.getCompoundTag("edges")
                    for (type in PipeType.values()) {
                        if (!edges.hasKey(type.name))
                            continue

                        val edgeList = edges.getTagList(type.name, 9)
                        for (edge in 0 until edgeList.tagCount()) {
                            val vertexes = edgeList.get(edge) as NBTTagCompound
                            val source = NBTUtil.getPosFromTag(vertexes.getCompoundTag("source"))
                            val target = NBTUtil.getPosFromTag(vertexes.getCompoundTag("target"))

                            network.loadEdge(source, target, networkId!!, world, type)
                        }
                    }
                }

                if (networkComponent.hasKey("firstBlock")) {
                    network.loadNode(BlockPos.fromLong(networkComponent.getLong("firstBlock")), networkId!!, world)
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
                    vertices.appendTag(NBTUtil.createPosTag(pos))
                }
                networkCompound.setTag("vertices", vertices)

                val edges = NBTTagCompound()
                for (type in PipeType.values()) {
                    val edgeList = NBTTagList()

                    println(blocks.edgeSet().size)

                    blocks.edgeSet().filter { it.pipeType == type }.forEach { edge ->
                        val edgeVertices = NBTTagCompound()

                        val src = blocks.getEdgeSource(edge)
                        val target = blocks.getEdgeTarget(edge)

                        edgeVertices.setTag("source", NBTUtil.createPosTag(src))
                        edgeVertices.setTag("target", NBTUtil.createPosTag(target))

                        edgeList.appendTag(edgeVertices)
                    }

                    if (edgeList.tagCount() != 0)
                        edges.setTag(type.name, edgeList)
                }
                networkCompound.setTag("edges", edges)

                worldCompound.appendTag(networkCompound)
            }

            compound.setTag(mapname, worldCompound)

        }

        return compound
    }
}