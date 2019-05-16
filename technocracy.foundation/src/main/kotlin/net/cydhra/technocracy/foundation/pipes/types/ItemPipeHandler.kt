package net.cydhra.technocracy.foundation.pipes.types

import net.cydhra.technocracy.foundation.pipes.FilteredPipeTypeGraph
import net.cydhra.technocracy.foundation.pipes.WrappedBlockPos
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import org.jgrapht.traverse.ClosestFirstIterator


object ItemPipeHandler : IPipeHandler {
    override val type: PipeType
        get() = PipeType.ITEM

    override fun handle(world: World, currentNode: WrappedBlockPos, extractionPipe: TileEntityPipe,
            extractionTileEntity: TileEntity, extractionFacing: EnumFacing, graph: FilteredPipeTypeGraph): Int {

        val iterator = ClosestFirstIterator(graph, currentNode)

        //output should not be split equal to all inputs, just find the best exit node
        for (node in iterator) {
            if (!node.hasIO) continue
            val inputs = node.getInputFacings(type, false)
            for (inputSide in inputs) {
                if (node == currentNode && extractionFacing == inputSide) continue

                val pipeIn = world.getTileEntity(node.pos) as TileEntityPipe
                val tileIn = world.getTileEntity(node.pos.offset(inputSide))!!

                if (extract(extractionPipe, pipeIn, extractionTileEntity, tileIn, extractionFacing, inputSide)) {
                    //TODO timeout

                    println("found output")

                    return 1
                }
            }
        }

        return 0
    }

    fun extract(pipeIn: TileEntityPipe, pipeOut: TileEntityPipe, tileIn: TileEntity, tileOut: TileEntity,
            facingIn: EnumFacing, facingOut: EnumFacing): Boolean {

        val capIn = tileIn.getCapability(type.capability!!, facingIn.opposite)
        val capOut = tileOut.getCapability(type.capability!!, facingOut.opposite)

        val handlerIn = capIn as IItemHandler
        val handlerOut = capOut as IItemHandler

        for (slotIn in 0 until handlerIn.slots) {
            //todo limit item transfer rate
            //todo item inout/output filtering
            val limit = 8
            var stackIn = handlerIn.extractItem(slotIn, limit, true)
            if (stackIn != ItemStack.EMPTY) {

                var maxItemOutput = 0
                for (slotOut in 0 until handlerOut.slots) {
                    val stackOut = handlerOut.insertItem(slotOut, stackIn, true)
                    if (stackOut.isEmpty) {
                        maxItemOutput += stackIn.count
                        break
                    }
                    maxItemOutput += stackIn.count - stackOut.count
                    stackIn = stackOut
                }

                if (maxItemOutput != 0) {
                    stackIn = handlerIn.extractItem(slotIn, maxItemOutput, false)
                    for (slotOut in 0 until handlerOut.slots) {
                        stackIn = handlerOut.insertItem(slotOut, stackIn, false)
                        if (stackIn.isEmpty) {
                            return true
                        }
                    }
                }
                /*for (slotOut in 0 until handlerOut.slots) {
                    val stackOut = handlerOut.insertItem(slotOut, stackIn, true)
                    if (stackOut.isEmpty || stackIn.count != stackOut.count) {
                        if (stackOut.isEmpty) {

                            handlerOut.insertItem(slotOut, stackIn, false)
                        } else {
                            limit = Math.min(limit, stackOut.count)
                            stackIn = handlerIn.extractItem(slotIn, limit, false)
                            handlerOut.insertItem(slotOut, stackIn, false)
                        }
                        return true
                    }
            }*/

                /* if (stackOut.isEmpty || stackIn.count != stackOut.count) {
                    if (stackOut.isEmpty) {
                        stackIn = handlerIn.extractItem(slotIn, limit, false)
                        handlerOut.insertItem(slotOut, stackIn, false)
                    } else {
                        limit = Math.min(limit, stackOut.count)
                        stackIn = handlerIn.extractItem(slotIn, limit, false)
                        handlerOut.insertItem(slotOut, stackIn, false)
                    }
                    return true
                }*/
                /*for (slotOut in 0 until handlerOut.slots) {
                val stackOut = handlerOut.insertItem(slotOut, stackIn, true)
                if (stackOut.isEmpty || stackIn.count != stackOut.count) {
                    if (stackOut.isEmpty) {
                        stackIn = handlerIn.extractItem(slotIn, limit, false)
                        handlerOut.insertItem(slotOut, stackIn, false)
                    } else {
                        limit = Math.min(limit, stackOut.count)
                        stackIn = handlerIn.extractItem(slotIn, limit, false)
                        handlerOut.insertItem(slotOut, stackIn, false)
                    }
                    return true
                }
            }*/

            }
        }

        return false
    }
}