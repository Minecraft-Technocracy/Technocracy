package net.cydhra.technocracy.astronautics.content.tileentity

import net.cydhra.technocracy.astronautics.client.gui.TabPlanetaryRendering
import net.cydhra.technocracy.astronautics.content.blocks.RocketControllerBlock
import net.cydhra.technocracy.astronautics.content.blocks.rocketHullBlock
import net.cydhra.technocracy.astronautics.content.blocks.rocketStorageBlock
import net.cydhra.technocracy.astronautics.content.entity.EntityRocket
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.ecs.IAggregatableGuiProvider
import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.button.DefaultButton
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.AggregatableTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityDataComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityOwnerShipComponent
import net.cydhra.technocracy.foundation.data.world.groups.GroupManager
import net.cydhra.technocracy.foundation.network.componentsync.GuiUpdateListener
import net.cydhra.technocracy.foundation.util.structures.Template
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import kotlin.math.ceil
import kotlin.math.min


class TileEntityRocketController : AggregatableTileEntity(), IAggregatableGuiProvider, TEInventoryProvider<DynamicInventoryCapability>, TCTileEntityGuiProvider, DynamicInventoryCapability.CustomItemStackStackLimit {

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun getStackLimit(slot: Int, stack: ItemStack, default: Int): Int {

        return maxStackSize.getValue()
        /*
        if (currentRocket != null) {
            if (!currentRocket!!.dysonCargo)
                return 1
        }

        //Todo limit some kinds of cargo to one per slot
        //current dyson cargo is 16 per cargo element, max rocket is 6 modules * 8 storage slots * 16 items = 768 max dyson parts per rocket
        return 16//default*/
    }

    val ownerShip = TileEntityOwnerShipComponent()
    val linked = TileEntityDataComponent(false)
    val maxStackSize = TileEntityDataComponent(0)// TileEntityIntegerDataComponent()
    val dynCapability = DynamicFluidCapability(0, mutableListOf("rocket_fuel"))
    val fluidBuffer = TileEntityFluidComponent(dynCapability, EnumFacing.values().toMutableSet())
    val inventoryBuffer = TileEntityInventoryComponent(
        0,
        this,
        EnumFacing.values().toMutableSet(),
        DynamicInventoryCapability.InventoryType.INPUT
    )

    var currentRocket: EntityRocket? = null

    fun linkToCurrentRocket(rocket: EntityRocket): Boolean {
        if (currentRocket == null || currentRocket!!.isDead) {
            currentRocket = rocket

            //forward capability to entity
            fluidBuffer.fluid = rocket.tank.fluid

            inventoryBuffer.inventory.stacks = rocket.cargoSlots!!
            inventoryBuffer.inventory.forceSlotTypes(DynamicInventoryCapability.InventoryType.BOTH)
            fluidBuffer.tile = this

            return true
        }
        return false
    }

    override fun getGui(player: EntityPlayer?, other: TCGui?): TCGui {

        val gui = other ?: SimpleGui(guiHeight = 230, container = TCContainer(this))
        gui.registerTab(object :
            TCTab("${getBlockType().localizedName} linked: ${currentRocket != null}", gui, -1, TCIcon(this.blockType)) {

            override fun init() {

                if (linked.getValue()) {
                    val fm = DefaultFluidMeter(10, 25, fluidBuffer, gui)
                    fm.width = 20
                    fm.height = 105

                    val maxSlotsPerRow = 7
                    var maxSlots = inventoryBuffer.inventory.size
                    val rows = ceil(maxSlots / maxSlotsPerRow.toDouble()).toInt()

                    for (row in 0 until rows) {
                        val currSlots = min(maxSlotsPerRow, maxSlots)
                        for (slot in 0 until currSlots) {
                            this.components.add(TCSlotIO(inventoryBuffer.inventory, slot + row * maxSlotsPerRow,
                                    35 + slot * 18, 26 + row * 18, parent))
                        }
                        maxSlots -= maxSlotsPerRow
                    }

                    components.add(fm)

                    components.add(DefaultButton(35, 25 + 105 - 20, gui.origWidth - 5 - 35, 20, "Remove Rocket", parent) { side, player, tileEntity, button ->
                        if (!player.isUser) {
                            (tileEntity as TileEntityRocketController).currentRocket!!.liftOff = true
                            /*
                            linked.setState(false)
                            //force update to be send
                            GuiUpdateListener.syncComponentsToClients()
                            //reopen gui to apply change
                            player.openGui(TCFoundation, TCGuiHandler.machineGui, player.world, pos.x, pos.y, pos.z)

                            world.removeEntity(currentRocket)*/
                        }
                    })

                } else {
                    /*components.add(RedRoundButton(5, 9 + 50 + 10, gui.origWidth - 8, 20, "Build Rocket") { player, tileEntity, button ->
                        //on server only
                        if (!player.isUser) {

                        }
                    })*/

                    components.add(DefaultButton(5, 9, gui.origWidth - 8, 20, "Build Rocket", parent) { side, player, tileEntity, button ->
                        //on server only
                        if (!player.isUser) {
                            constructRocket(player, player.world)
                        }
                    })
                }

                if (player != null) {
                    //stick to bottom
                    //58 => last row
                    //16 => size of the last row
                    //5 => spacer
                    //12 => font height + 3px spacer
                    addPlayerInventorySlots(player, 8, gui.origHeight - 58 - 16 - 5 - 12)
                }
            }
        })

        gui.registerTab(TabPlanetaryRendering(gui))

        return gui
    }

    fun render() {

    }

    fun constructRocket(playerIn: EntityPlayer, worldIn: World) {
        if (ownerShip.currentOwner == null) {
            ownerShip.setOwnerShip(GroupManager.getGroupFromUser(playerIn.uniqueID))
        }

        if (ownerShip.currentOwner!!.getRights(playerIn.uniqueID) == GroupManager.PlayerGroup.GroupRights.NONE) {
            playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.ownership"))
        }

        if (!RocketControllerBlock.launchpad.init) {
            RocketControllerBlock.launchpad.loadFromAssets("launchpad")
            RocketControllerBlock.rocket_base.loadFromAssets("rocket/rocket_base")
            RocketControllerBlock.rocket_tip_a.loadFromAssets("rocket/rocket_tip_a")
            RocketControllerBlock.rocket_tip_b.loadFromAssets("rocket/rocket_tip_b")
            RocketControllerBlock.tank_module.loadFromAssets("rocket/tank_module")
            RocketControllerBlock.dyson_cargo.loadFromAssets("rocket/storage_module")
            RocketControllerBlock.satellite_cargo.loadFromAssets("rocket/satellite_module")
        }

        if (currentRocket != null) {
            playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.already_linked"))
        }

        val matches = RocketControllerBlock.launchpad.matches(worldIn, pos, true)

        if (matches != null) {
            val base = RocketControllerBlock.rocket_base.matches(worldIn, pos, true)
            if (base != null) {

                val rotation = base.first

                val blocks = mutableListOf<BlockPos>()
                blocks.addAll(base.second)

                var offPos = pos.add(0, 4, 0)

                var tip = false
                var index = 0

                var totalStorageElements = 0
                var dysonCargo = 0
                var satelliteCargo = 0

                var tank = 0

                while (!tip && index < 10) {
                    index++

                    val match_tank = RocketControllerBlock.tank_module.matches(worldIn, offPos, true)
                    if (match_tank != null) {
                        blocks.addAll(match_tank.second)
                        if (totalStorageElements != 0) {
                            break
                        }

                        tank++
                        offPos = offPos.add(0, 3, 0)
                        continue
                    }

                    val match_storage = RocketControllerBlock.dyson_cargo.matches(worldIn, offPos, true, valid = { _, block, _ ->
                        if (block == rocketStorageBlock) {
                            dysonCargo++
                        }
                        block == rocketHullBlock || block == rocketStorageBlock
                    })

                    if (match_storage != null) {
                        blocks.addAll(match_storage.second)
                        totalStorageElements++
                        offPos = offPos.add(0, 3, 0)
                        continue
                    }

                    val match_satelite = RocketControllerBlock.satellite_cargo.matches(worldIn, offPos, true)

                    if (match_satelite != null) {
                        blocks.addAll(match_satelite.second)
                        totalStorageElements++
                        satelliteCargo++
                        offPos = offPos.add(0, 3, 0)
                        continue
                    }

                    //todo add rocket controller
                    var match_tip = RocketControllerBlock.rocket_tip_a.matches(worldIn, offPos, true, valid = { check_state, block, _ -> block == rocketHullBlock })

                    if (match_tip != null) {
                        blocks.addAll(match_tip.second)
                        tip = true
                        continue
                    }

                    match_tip = RocketControllerBlock.rocket_tip_b.matches(worldIn, offPos, true, valid = { check_state, block, _ -> block == rocketHullBlock })
                    if (match_tip != null) {
                        blocks.addAll(match_tip.second)
                        tip = true
                        continue
                    }
                }

                if (satelliteCargo != 0 && dysonCargo != 0) {
                    playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.cantMix"))
                    return
                }

                if (tip && tank * 2 >= totalStorageElements) {

                    //offset the position towards the rocket
                    val offset = pos.offset(rotation.rotate(EnumFacing.NORTH), -3)

                    val template = Template(offset, worldIn, blocks)
                    val ent = EntityRocket(worldIn, template, pos, ownerShip.currentOwner!!)
                    //ent.motionY = 0.005
                    ent.setPosition(offset.x + 0.5, offset.y.toDouble(), offset.z + 0.5)
                    worldIn.spawnEntity(ent)

                    //16 buckets base rocket + 16 buckets for each tank module

                    ent.tank.fluid.capacity = (16 + tank * 16) * 1000

                    if (dysonCargo != 0) {
                        this.maxStackSize.setValue(1)
                        ent.dysonCargo = true
                        ent.cargoSlots = NonNullList.withSize(dysonCargo + 20, ItemStack.EMPTY)
                    } else {
                        this.maxStackSize.setValue(16)
                        ent.cargoSlots = NonNullList.withSize(totalStorageElements + 20, ItemStack.EMPTY)
                    }

                    //ent.liftOff = true

                    linkToCurrentRocket(ent)

                    for (e in blocks) {
                        worldIn.setBlockToAir(e)
                    }

                    playerIn.sendMessage(TextComponentString("rocket build: $totalStorageElements storage modules with $dysonCargo elements and $tank tank modules"))
                    linked.setValue(true)

                    //force update to be send
                    GuiUpdateListener.syncComponentsToClients()
                    //reopen gui to apply change
                    playerIn.openGui(TCFoundation, TCGuiHandler.machineGui, worldIn, pos.x, pos.y, pos.z)

                    return
                }
            }
            playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.rocket"))
        } else {
            playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.launchpad"))
        }
    }

    fun unlinkRocket() {
        linked.setValue(false)
        currentRocket = null
        fluidBuffer.fluid = dynCapability
        inventoryBuffer.inventory.stacks = NonNullList.withSize(0, ItemStack.EMPTY)
        this.markForUpdate()
    }

    init {
        registerComponent(ownerShip, "ownership")
        registerComponent(linked, "linked")
        registerComponent(maxStackSize, "maxStackSize")
        registerComponent(fluidBuffer, "fluidBuffer")
        registerComponent(inventoryBuffer, "invBuffer")
        //no need to save or update as it only references to the entity
        linked.allowAutoSave = false
        fluidBuffer.allowAutoSave = false
        inventoryBuffer.allowAutoSave = false
    }

    override fun canInteractWith(player: EntityPlayer?): Boolean {
        if (player == null) return true
        return player.isEntityAlive && !tile.isInvalid && player.getDistanceSq(tile.pos) <= 16
    }
}