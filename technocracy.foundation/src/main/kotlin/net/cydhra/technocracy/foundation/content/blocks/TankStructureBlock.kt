package net.cydhra.technocracy.foundation.content.blocks

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.cydhra.technocracy.foundation.api.tileentities.TCMultiBlockActiveTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.tank.TileEntityTankMultiBlockPart
import net.cydhra.technocracy.foundation.model.blocks.impl.PlainMultiBlockPartBlock
import net.cydhra.technocracy.foundation.util.propertys.DIMENSIONS
import net.cydhra.technocracy.foundation.util.propertys.FLUIDSTACK
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.cydhra.technocracy.foundation.util.propertys.TANKSIZE
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import javax.vecmath.Vector3f

/**
 * Part of the tank multiblock structure. Unlike other multiblock structure blocks, this one has special behaviour to
 * allow storing parts of the multiblock state within one of its blocks
 */
class TankStructureBlock<T>(unlocalizedName: String,
                            tileEntityConstructor: () -> T,
                            opaque: Boolean = true,
                            isFullCube: Boolean = true,
                            glassSides: Boolean = false,
                            renderLayer: BlockRenderLayer = BlockRenderLayer.SOLID)
    : PlainMultiBlockPartBlock<T>(unlocalizedName, tileEntityConstructor, opaque, isFullCube, glassSides, renderLayer)
        where T : TileEntity, T : TCMultiBlockActiveTileEntity, T : IMultiblockPart {

    init {
        setHardness(3f)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {
            val controllerTileEntity = getMultiBlockPartTileEntity(worldIn, pos)
            if (controllerTileEntity.validateStructure()) {
                controllerTileEntity.onActivate(worldIn, pos, playerIn, hand, facing)
                return true
            }
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): IExtendedBlockState {
        if (world != null && pos != null) {
            val tile = world.getTileEntity(pos) as? TileEntityTankMultiBlockPart
                    ?: return (state as IExtendedBlockState).withProperty(FLUIDSTACK, null).withProperty(DIMENSIONS, null).withProperty(TANKSIZE, null).withProperty(POSITION, null)
            if (tile.fluidComp.isAttached && tile.multiblockController != null && tile.multiblockController!!.isAssembled && tile.multiblockController!!.controllerTileEntity == tile && tile.fluidComp.innerComponent.fluid.currentFluid != null) {
                val minimumCoord = tile.multiblockController!!.minimumCoord!!
                val maximumCoord = tile.multiblockController!!.maximumCoord!!
                val sizeX = maximumCoord.x - minimumCoord.x + 1f
                val sizeY = maximumCoord.y - minimumCoord.y + 1f
                val sizeZ = maximumCoord.z - minimumCoord.z + 1f
                return (state as IExtendedBlockState).withProperty(FLUIDSTACK, tile.fluidComp.innerComponent.fluid.currentFluid)
                        .withProperty(DIMENSIONS, Vector3f(sizeX, sizeY, sizeZ))
                        .withProperty(TANKSIZE, tile.fluidComp.innerComponent.fluid.capacity as Integer)
                        .withProperty(POSITION, pos)
            }
        }

        return (state as IExtendedBlockState).withProperty(FLUIDSTACK, null).withProperty(DIMENSIONS, null).withProperty(TANKSIZE, null).withProperty(POSITION, null)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(FLUIDSTACK).add(DIMENSIONS).add(TANKSIZE).add(POSITION).build()
    }

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        return true
    }
}