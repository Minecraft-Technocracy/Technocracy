package net.cydhra.technocracy.astronautics.content.tileentity

import net.cydhra.technocracy.astronautics.content.entity.EntityRocket
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.OwnerShipComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

class TileEntityRocketController : AggregatableTileEntity(), TEInventoryProvider {

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack) {
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return true
    }

    val ownerShip = OwnerShipComponent()
    val dynCapability = DynamicFluidCapability(0, mutableListOf("rocket_fuel"))
    val fluidBuffer = FluidComponent(dynCapability, EnumFacing.values().toMutableSet())
    val inventoryBuffer = InventoryComponent(3, this, EnumFacing.values().toMutableSet())

    var currentRocket: EntityRocket? = null

    fun linkToCurrentRocket(rocket: EntityRocket): Boolean {
        if (currentRocket == null || currentRocket!!.isDead) {
            currentRocket = rocket

            //forward capability to entity
            fluidBuffer.fluid = rocket.tank.fluid
            return true
        }
        return false
    }

    fun unlinkRocket() {
        currentRocket = null
        fluidBuffer.fluid = dynCapability
    }

    init {
        registerComponent(ownerShip, "ownership")
        registerComponent(fluidBuffer, "fluidBuffer")
        registerComponent(inventoryBuffer, "invBuffer")
        //no need to save or update as it only references to the entity
        fluidBuffer.allowAutoSave = false
    }
}