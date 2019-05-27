package net.cydhra.technocracy.foundation.client.renderer.tileEntity

import net.cydhra.technocracy.foundation.blocks.general.pipe
import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.pipes.types.PipeType
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.cydhra.technocracy.foundation.util.opengl.OpenGLBoundingBox
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d

class PipeRenderer : TileEntitySpecialRenderer<TileEntityPipe>() {

    companion object {
        const val size = 0.05
        const val nodeSize = 0.075

        val node = AxisAlignedBB(Vec3d((0.5 - nodeSize), (0.5 - nodeSize), (0.5 - nodeSize)),
                Vec3d((0.5 + nodeSize), (0.5 + nodeSize), (0.5 + nodeSize)))

        val connections = mapOf(EnumFacing.NORTH to AxisAlignedBB(Vec3d((0.5 - size), (0.5 - size), 0.0),
                Vec3d((0.5 + size), (0.5 + size), (0.5 - size))),
                EnumFacing.SOUTH to AxisAlignedBB(Vec3d((0.5 - size), (0.5 - size), (0.5 + size)),
                        Vec3d((0.5 + size), (0.5 + size), 1.0)),
                EnumFacing.EAST to AxisAlignedBB(Vec3d((0.5 + size), (0.5 - size), (0.5 - size)),
                        Vec3d(1.0, (0.5 + size), (0.5 + size))),
                EnumFacing.WEST to AxisAlignedBB(Vec3d(0.0, (0.5 - size), (0.5 - size)),
                        Vec3d((0.5 - size), (0.5 + size), (0.5 + size))),
                EnumFacing.UP to AxisAlignedBB(Vec3d((0.5 - size), (0.5 + size), (0.5 - size)),
                        Vec3d((0.5 + size), 1.0, (0.5 + size))),
                EnumFacing.DOWN to AxisAlignedBB(Vec3d((0.5 - size), 0.0, (0.5 - size)),
                        Vec3d((0.5 + size), (0.5 - size), (0.5 + size))))
    }

    override fun render(te: TileEntityPipe, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int,
            alpha: Float) {
        val pos = te.pos

        GlStateManager.pushMatrix()

        GlStateManager.color(1F, 1F, 1F, 1F)

        //Translate already interpolated positions
        GlStateManager.translate(x, y, z)

        //populate connected facings
        val facings = mutableSetOf<EnumFacing>()
        te.getInstalledTypes().sorted().forEach { type ->
            connections.forEach { (facing, _) ->
                if (world.getBlockState(pos.offset(facing)).block == pipe) {
                    val neighbourPipe = (world.getTileEntity(pos.offset(facing)) as TileEntityPipe)
                    val connected = neighbourPipe.getInstalledTypes().contains(type)

                    if (connected) {
                        facings.add(facing)
                    }
                }
            }
        }

        //distinguish multiple entries using a set and decide if a connection is straight or goes around a corner
        val straight = facings.map { it.axis }.toSet().size == 1

        //Render all nodes and connections
        te.getInstalledTypes().sorted().forEachIndexed { index, type ->
            connections.forEach { (facing, boundingBox) ->
                var nodeConnectionOffset =
                        (index * node.averageEdgeLength) - ((te.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2

                //The block is a pipe
                if (world.getBlockState(pos.offset(facing)).block == pipe) {
                    val neighbourPipe = (world.getTileEntity(pos.offset(facing)) as TileEntityPipe)
                    val connected = neighbourPipe.getInstalledTypes().contains(type)

                    //Is connected in any way to the neighbour pipe
                    if (connected) {
                        GlStateManager.pushMatrix()

                        //neighbour has less types
                        if (neighbourPipe.getInstalledTypes().size < te.getInstalledTypes().size) {
                            //use offset of the neighbour
                            nodeConnectionOffset =
                                    if (te.getInstalledTypes().size != 1 && neighbourPipe.getInstalledTypes().size != 1) { // if both type sizes are not one some more calculations are needed
                                        var neighbourIndex = index

                                        //find the index of the neighbour pipe of the current type
                                        neighbourPipe.getInstalledTypes().sorted().forEachIndexed { idx, it ->
                                            if (it == type) {
                                                neighbourIndex = idx
                                            }
                                        }

                                        //apply the the offset with the current neighbour index
                                        (neighbourIndex * node.averageEdgeLength) - ((neighbourPipe.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2
                                    } else {
                                        ((neighbourPipe.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2 //Apply offset for one installed type
                                    }
                        } else if (neighbourPipe.getInstalledTypes().size == te.getInstalledTypes().size) { //if has same size of types
                            var sameTypeCount = 0

                            //calculate the amount of types both lists have
                            neighbourPipe.getInstalledTypes().sorted().forEach {
                                if (te.getInstalledTypes().contains(it)) {
                                    sameTypeCount++
                                }
                            }

                            if (sameTypeCount == 1) { //both have exactly one same type
                                nodeConnectionOffset = 0.0 //no offset is needed at all
                            }
                        }

                        //Translate to the offset according to the axis of the connection
                        when {
                            facing.axis == EnumFacing.Axis.X -> GlStateManager.translate(0.0, 0.0, nodeConnectionOffset)
                            facing.axis == EnumFacing.Axis.Z -> GlStateManager.translate(-nodeConnectionOffset,
                                    0.0,
                                    0.0)
                            facing.axis.isVertical -> GlStateManager.translate(0.0, 0.0, nodeConnectionOffset)
                        }

                        //Draw connection
                        OpenGLBoundingBox.drawTexturedBoundingBox(boundingBox, this.getTextureForConnectionType(type))

                        //Draw node
                        if (straight) {
                            OpenGLBoundingBox.drawTexturedBoundingBox(node, this.getTextureForNodeType(type))
                        }

                        GlStateManager.popMatrix()
                    }
                }
            }

            //Draw main node
            if (!straight) {
                val expansion = ((te.getInstalledTypes().size - 1) * node.averageEdgeLength) / 2
                OpenGLBoundingBox.drawTexturedBoundingBox(node.expand(expansion * 2,
                        0.0,
                        expansion * 2).offset(-expansion, 0.0, -expansion), this.getTextureForNodeType(type))
            } else {
                OpenGLBoundingBox.drawTexturedBoundingBox(node, this.getTextureForNodeType(type))
            }
        }

        GlStateManager.popMatrix()
    }

    private fun getTextureForConnectionType(type: PipeType): TextureAtlasSprite {
        return when (type) {
            PipeType.ENERGY -> TextureAtlasManager.pipe_energy
            PipeType.FLUID -> TextureAtlasManager.pipe_fluid
            PipeType.ITEM -> TextureAtlasManager.pipe_item
        }
    }

    private fun getTextureForNodeType(type: PipeType): TextureAtlasSprite {
        return TextureAtlasManager.pipe_node
    }
}