package net.cydhra.technocracy.astronautics.content.blocks

import net.cydhra.technocracy.astronautics.content.tileentity.TileEntityRocketController
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.data.world.groups.GroupManager
import net.cydhra.technocracy.foundation.model.blocks.api.AbstractRotatableTileEntityBlock
import net.cydhra.technocracy.foundation.model.blocks.util.IDynamicBlockPlaceBehavior
import net.cydhra.technocracy.foundation.util.structures.Template
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World


class RocketControllerBlock : AbstractRotatableTileEntityBlock("rocket_controller", material = Material.ROCK), IDynamicBlockPlaceBehavior {

    init {
        this.setHardness(2f)
        this.setResistance(4f)
    }

    override fun placeBlockAt(place: Boolean, stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState): Boolean {
        val tile = world.getTileEntity(pos) as? TileEntityRocketController ?: return place

        if (!world.isRemote)
            tile.ownerShip.setOwnerShip(GroupManager.getGroupFromUser(player.uniqueID))

        return place
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityRocketController()
    }

    companion object {
        val launchpad = Template()
        val rocket_base = Template()
        val rocket_tip_a = Template()
        val rocket_tip_b = Template()
        val tank_module = Template()
        val dyson_cargo = Template()
        val satellite_cargo = Template()
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {
            if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND) {
                playerIn.openGui(TCFoundation, TCGuiHandler.machineGui, worldIn, pos.x, pos.y, pos.z)
            }

            return true
        }

        if (true)
            return true


        if (worldIn.isRemote || hand != EnumHand.MAIN_HAND) {

            //TCParticleManager.addParticle(LaserBeam(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ))


            return true//super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
        }

        val tile = worldIn.getTileEntity(pos) as TileEntityRocketController


        return true
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack(this)
    }
}