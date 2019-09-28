package net.cydhra.technocracy.astronautics.blocks

import net.cydhra.technocracy.astronautics.blocks.general.rocketHullBlock
import net.cydhra.technocracy.astronautics.blocks.general.rocketStorageBlock
import net.cydhra.technocracy.astronautics.entity.EntityRocket
import net.cydhra.technocracy.astronautics.tileentity.RocketControllerTileEntity
import net.cydhra.technocracy.foundation.blocks.api.AbstractRotatableTileEntityBlock
import net.cydhra.technocracy.foundation.data.OwnershipManager
import net.cydhra.technocracy.foundation.util.structures.Template
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


class RocketControllerBlock : AbstractRotatableTileEntityBlock("rocket_controller", material = Material.ROCK) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return RocketControllerTileEntity()
    }

    val launchpad = Template()
    val rocket_base = Template()
    val rocket_tip_a = Template()
    val rocket_tip_b = Template()
    val tank_module = Template()
    val storage_module = Template()

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote || hand == EnumHand.MAIN_HAND)
            return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)

        val ownership = OwnershipManager.getUserGroup(playerIn.uniqueID)

        if(ownership.getRights(playerIn.uniqueID) == OwnershipManager.Ownership.OwnershipRights.OWNER) {
            println("IS OWNER")
        }

        if (!launchpad.init) {
            launchpad.loadFromAssets("launchpad")
            rocket_base.loadFromAssets("rocket/rocket_base")
            rocket_tip_a.loadFromAssets("rocket/rocket_tip_a")
            rocket_tip_b.loadFromAssets("rocket/rocket_tip_b")
            tank_module.loadFromAssets("rocket/tank_module")
            storage_module.loadFromAssets("rocket/storage_module")
        }

        val matches = launchpad.matches(worldIn, pos, true)

        if (matches != null) {


            val base = rocket_base.matches(worldIn, pos, true)
            if (base != null) {

                val blocks = mutableListOf<BlockPos>()
                blocks.addAll(base)

                var offPos = pos.add(0, 4, 0)

                var tip = false
                var index = 0

                var storage = 0
                var singleStorage = 0

                var tank = 0

                while (!tip && index < 10) {
                    index++

                    val match_tank = tank_module.matches(worldIn, offPos, true)
                    if (match_tank != null) {
                        blocks.addAll(match_tank)
                        if (storage != 0) {
                            break
                        }

                        tank++
                        offPos = offPos.add(0, 3, 0)
                        continue
                    }

                    val match_storage = storage_module.matches(worldIn, offPos, true, valid = { _, block, _ ->
                        if (block == rocketStorageBlock) {
                            singleStorage++
                        }
                        block == rocketHullBlock || block == rocketStorageBlock
                    })
                    if (match_storage != null) {
                        blocks.addAll(match_storage)
                        storage++
                        offPos = offPos.add(0, 3, 0)
                        continue
                    }

                    //todo add rocket controller
                    var match_tip = rocket_tip_a.matches(worldIn, offPos, true, valid = { check_state, block, _ -> block == rocketHullBlock })

                    if (match_tip != null) {
                        blocks.addAll(match_tip)
                        tip = true
                        continue
                    }

                    match_tip = rocket_tip_b.matches(worldIn, offPos, true, valid = { check_state, block, _ -> block == rocketHullBlock })
                    if (match_tip != null) {
                        blocks.addAll(match_tip)
                        tip = true
                        continue
                    }
                }

                if (tip && tank * 2 >= storage) {

                    val template = Template(pos, worldIn, blocks)
                    val ent = EntityRocket(worldIn, template, pos)
                    ent.motionY = 0.005
                    ent.setPosition(pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5)
                    worldIn.spawnEntity(ent)

                    playerIn.sendMessage(TextComponentString("rocket build: $storage storage modules with $singleStorage elements and $tank tank modules"))
                }
            }

        }


        return false
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack(this)
    }
}