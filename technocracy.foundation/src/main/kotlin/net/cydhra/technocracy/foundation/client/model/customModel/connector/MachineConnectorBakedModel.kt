package net.cydhra.technocracy.foundation.client.model.customModel.connector

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.components.InventoryComponent
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

        val blockState = state as IExtendedBlockState
        val clean = blockState.clean

        //add the model quds to a list
        val quads = mutableListOf<BakedQuad>()
        quads.addAll(baseBakedModel.getQuads(clean, side, rand))

        //get the models with the texture
        val inventory = getModelWithTexture(clean, connector, TextureAtlasManager.connector_inventory!!)
        val energy = getModelWithTexture(clean, connector, TextureAtlasManager.connector_energy!!)

        //get the position of this model out of the invisible property
        val pos = blockState.getValue(POSITION)

        //use direct call to world, as this is client side and thus can only be in the currently loaded world
        val tile = Minecraft.getMinecraft().world.getTileEntity(pos) ?: return quads
        val machine = tile as MachineTileEntity

        //get all components of the TileEntity
        val comp = machine.getComponents()

        comp.stream().filter {
            it.second is InventoryComponent
        }.map { it.second as InventoryComponent }.forEach {
            for ((i, quad) in inventory.getQuads(state, side, rand).withIndex()) {
                //6 faces per cube
                val facing = EnumFacing.values()[i / 6]
                //TODO custom colored inventorys?
                //render connector on the side it should be
                if (facing == it.facing)
                    quads.add(quad)
            }
        }

        comp.stream().filter {
            it.second is EnergyStorageComponent
        }.map { it.second as EnergyStorageComponent }.forEach {
            for ((i, quad) in energy.getQuads(state, side, rand).withIndex()) {
                //6 faces per cube
                val facing = EnumFacing.values()[i / 6]
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