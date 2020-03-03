package net.cydhra.technocracy.foundation.model.blocks.impl

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.model.blocks.api.AbstractRotatableTileEntityBlock
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.network.componentsync.guiInfoPacketSubscribers
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState

class MachineBlock(name: String, private val tileEntityConstructor: () -> TileEntity)
    : AbstractRotatableTileEntityBlock(name, material = Material.ROCK) {

    override fun addExtendedPropertyToState(state: IExtendedBlockState, world: IBlockAccess?, pos: BlockPos?): IExtendedBlockState {
        return state.withProperty(POSITION, pos)
    }

    override fun addPropertyToBuilder(builder: BlockStateContainer.Builder): BlockStateContainer.Builder {
        return builder.add(POSITION)
    }

    init {
        this.setHardness(2f)
        this.setResistance(4f)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return tileEntityConstructor()
    }

    @Suppress("OverridingDeprecatedMember")
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {
            if (!worldIn.isRemote) {
                playerIn.openGui(TCFoundation, TCGuiHandler.machineGui, worldIn, pos.x, pos.y, pos.z)
            }

            return true
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack(this)
    }
}