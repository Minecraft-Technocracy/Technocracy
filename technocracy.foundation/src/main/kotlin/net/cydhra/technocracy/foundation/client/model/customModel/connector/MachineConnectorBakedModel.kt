package net.cydhra.technocracy.foundation.client.model.customModel.connector

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.SimpleBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.property.IExtendedBlockState


class MachineConnectorBakedModel(val baseBakedModel: IBakedModel, val connector: IBakedModel) : IBakedModel by baseBakedModel {

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {

        if(state !is IExtendedBlockState) {
            return baseBakedModel.getQuads(state, side, rand)
        }

        val clean = state.clean

        //add the model quds to a list
        val quads = mutableListOf<BakedQuad>()
        quads.addAll(baseBakedModel.getQuads(clean, side, rand))

        //get the models with the texture
        val inventory = getModelWithTexture(clean, connector, TextureAtlasManager.connector_inventory)
        val energy = getModelWithTexture(clean, connector, TextureAtlasManager.connector_energy)

        //get the position of this model out of the invisible property
        val pos = state.getValue(POSITION) ?: return quads

        //use direct call to world, as this is client side and thus can only be in the currently loaded world
        val tile = Minecraft.getMinecraft().world.getTileEntity(pos) as? MachineTileEntity
                ?: return quads

        //get all components of the TileEntity
        val comp = tile.getComponents()

        comp.stream().filter {
            it.second is InventoryTileEntityComponent
        }.map { it.second as InventoryTileEntityComponent }.forEach {
            for ((i, quad) in inventory.getQuads(state, side, rand).withIndex()) {
                //6 faces per cube
                var facing = EnumFacing.values()[i / 6]
                if(facing.axis == EnumFacing.Axis.Z)
                    facing = facing.opposite
                //TODO custom colored inventorys?
                //render connector on the side it should be
                if (facing == it.facing)
                    quads.add(quad)
            }
        }

        comp.stream().filter {
            it.second is EnergyStorageTileEntityComponent
        }.map { it.second as EnergyStorageTileEntityComponent }.forEach {
            for ((i, quad) in energy.getQuads(state, side, rand).withIndex()) {
                //6 faces per cube
                var facing = EnumFacing.values()[i / 6]
                if(facing.axis == EnumFacing.Axis.Z)
                    facing = facing.opposite
                //render connector on the side it should be
                if (it.facing.contains(facing))
                    quads.add(quad)
            }
        }
        return quads
    }

    fun getModelWithTexture(state: IBlockState, model: IBakedModel, texture: TextureAtlasSprite): IBakedModel {
        return SimpleBakedModel.Builder(state, model, texture, BlockPos.ORIGIN).makeBakedModel()
    }
}