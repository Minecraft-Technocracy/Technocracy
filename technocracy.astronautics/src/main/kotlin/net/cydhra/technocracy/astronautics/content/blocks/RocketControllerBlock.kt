package net.cydhra.technocracy.astronautics.content.blocks

import net.cydhra.technocracy.astronautics.content.tileentity.TileEntityRocketController
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.blocks.util.IDynamicBlockPlaceBehavior
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.content.blocks.AbstractRotatableTileEntityBlock
import net.cydhra.technocracy.foundation.data.world.groups.GroupManager
import net.cydhra.technocracy.foundation.util.sendInfoMessage
import net.cydhra.technocracy.foundation.util.structures.Template
import net.cydhra.technocracy.foundation.util.structures.Template.Companion.toStructure
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
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
                val tile = worldIn.getTileEntity(pos) as? TileEntityRocketController ?: return false

                if (!launchpad.init) {
                    launchpad.init = true
                    launchpad.loadFromAssets("launchpad")
                    rocket_base.loadFromAssets("rocket/rocket_base")
                    rocket_tip_a.loadFromAssets("rocket/rocket_tip_a")
                    rocket_tip_b.loadFromAssets("rocket/rocket_tip_b")
                    tank_module.loadFromAssets("rocket/tank_module")
                    dyson_cargo.loadFromAssets("rocket/storage_module")
                    satellite_cargo.loadFromAssets("rocket/satellite_module")
                }

                if (!tile.baseStructure.isAttached) {
                    val matches = launchpad.matches(worldIn, pos, true)

                    if (matches != null) {
                        tile.baseStructure.isAttached = true
                        tile.baseStructure.markDirty(false)
                        matches.toStructure(tile.baseStructure.innerComponent.getValue(), worldIn, playerIn, pos)
                    } else {
                        playerIn.sendInfoMessage(TextComponentString("The Launchpad is invalid"))
                        return true
                    }
                }

                playerIn.openGui(TCFoundation, TCGuiHandler.machineGui, worldIn, pos.x, pos.y, pos.z)
            }
        }

        return true
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack(this)
    }
}