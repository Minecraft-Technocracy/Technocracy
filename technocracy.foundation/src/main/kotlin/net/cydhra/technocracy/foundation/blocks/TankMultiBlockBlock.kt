package net.cydhra.technocracy.foundation.blocks

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import net.cydhra.technocracy.foundation.tileentity.api.TCMultiBlockActiveTileEntity
import net.cydhra.technocracy.foundation.tileentity.multiblock.tank.TileEntityTankMultiBlockPart
import net.cydhra.technocracy.foundation.util.propertys.FLUIDSTACK
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.IExtendedBlockState


class TankMultiBlockBlock<T>(unlocalizedName: String,
                             tileEntityConstructor: () -> T,
                             opaque: Boolean = true,
                             isFullCube: Boolean = true,
                             glassSides: Boolean = false,
                             renderLayer: BlockRenderLayer = BlockRenderLayer.SOLID)
    : PlainMultiBlockPartBlock<T>(unlocalizedName, tileEntityConstructor, opaque, isFullCube, glassSides, renderLayer)
        where T : TileEntity, T : TCMultiBlockActiveTileEntity, T : IMultiblockPart {
    
    override fun getExtendedState(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): IExtendedBlockState {
        if (world != null && pos != null) {
            val tile = world.getTileEntity(pos) as? TileEntityTankMultiBlockPart
                    ?: return (state as IExtendedBlockState).withProperty(FLUIDSTACK, null)
            if (tile.fluidComp.isAttached) {
                return (state as IExtendedBlockState).withProperty(FLUIDSTACK, tile.fluidComp.innerComponent.fluid.currentFluid)
            }
        }

        return (state as IExtendedBlockState).withProperty(FLUIDSTACK, null)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(FLUIDSTACK).build()
    }
}