package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.general.BaseTileEntityBlock
import net.cydhra.technocracy.foundation.pipes.Network
import net.cydhra.technocracy.foundation.proxy.CommonProxy
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.World


class PipeBlock : BaseTileEntityBlock("pipe", material = Material.PISTON) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityPipe()
    }

    override fun getStateForPlacement(worldIn: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
        return defaultState
    }

    override fun onBlockDestroyedByExplosion(worldIn: World, pos: BlockPos, explosionIn: Explosion) {
        Network.removeNodeInEveryNetwork(pos, worldIn)
        super.onBlockDestroyedByExplosion(worldIn, pos, explosionIn)
    }

    override fun onBlockDestroyedByPlayer(worldIn: World, pos: BlockPos, state: IBlockState) {
        Network.removeNodeInEveryNetwork(pos, worldIn)
        super.onBlockDestroyedByPlayer(worldIn, pos, state)
    }
}