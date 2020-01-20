package net.cydhra.technocracy.astronautics.content.tileentity

import net.cydhra.technocracy.astronautics.content.entity.EntityRocket
import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.OwnerShipTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation

class TileEntityRocketController : AggregatableTileEntity(), TEInventoryProvider, TCTileEntityGuiProvider, DynamicInventoryCapability.CustomItemStackStackLimit {

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun getStackLimit(slot: Int, stack: ItemStack, default: Int): Int {
        if (currentRocket != null) {
            if (!currentRocket!!.dysonCargo)
                return 1
        }
        //Todo limit some kinds of cargo to one per slot
        //current dyson cargo is 16 per cargo element, max rocket is 6 modules * 8 storage slots * 16 items = 768 max dyson parts per rocket
        return 16//default
    }

    val ownerShip = OwnerShipTileEntityComponent()
    val dynCapability = DynamicFluidCapability(0, mutableListOf("rocket_fuel"))
    val fluidBuffer = FluidTileEntityComponent(dynCapability, EnumFacing.values().toMutableSet())
    val inventoryBuffer = InventoryTileEntityComponent(0, this, EnumFacing.values().toMutableSet())

    var currentRocket: EntityRocket? = null

    fun linkToCurrentRocket(rocket: EntityRocket): Boolean {
        if (currentRocket == null || currentRocket!!.isDead) {
            currentRocket = rocket

            //forward capability to entity
            fluidBuffer.fluid = rocket.tank.fluid

            inventoryBuffer.inventory.stacks = rocket.cargoSlots!!
            inventoryBuffer.inventory.forceSlotTypes(DynamicInventoryCapability.InventoryType.BOTH)

            return true
        }
        return false
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val gui = TCGui(guiHeight = 230, container = TCContainer(1, 1))
        gui.registerTab(object : TCTab("${getBlockType().localizedName} linked: ${currentRocket != null}", gui, -1,
                ResourceLocation("technocracy.foundation", "textures/item/gear.png")) {
            override fun init() {

                if (player != null) {
                    //stick to bottom
                    addPlayerInventorySlots(player, 8, gui.guiHeight - 58 - 16 - 5 - 12)
                }

                val fm = DefaultFluidMeter(10, 25, fluidBuffer, gui)
                fm.width = 20
                fm.height = 105

                components.add(fm)
            }
        })

        return gui
    }

    fun unlinkRocket() {
        currentRocket = null
        fluidBuffer.fluid = dynCapability
        inventoryBuffer.inventory.stacks = NonNullList.withSize(0, ItemStack.EMPTY)
    }

    init {
        registerComponent(ownerShip, "ownership")
        registerComponent(fluidBuffer, "fluidBuffer")
        registerComponent(inventoryBuffer, "invBuffer")
        //no need to save or update as it only references to the entity
        fluidBuffer.allowAutoSave = false
        inventoryBuffer.allowAutoSave = false
    }
}