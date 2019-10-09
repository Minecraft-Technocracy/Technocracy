package net.cydhra.technocracy.astronautics.tileentity

import net.cydhra.technocracy.astronautics.entity.EntityRocket
import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.tileentity.AggregatableTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.cydhra.technocracy.foundation.tileentity.components.InventoryComponent
import net.cydhra.technocracy.foundation.tileentity.components.OwnerShipComponent
import net.cydhra.technocracy.foundation.tileentity.management.TEInventoryProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

class TileEntityRocketController : AggregatableTileEntity(), TEInventoryProvider {

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack) {
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return true
    }

    val ownerShip = OwnerShipComponent()
    val fluidBuffer = FluidComponent(DynamicFluidCapability(16000, mutableListOf("rocket_fuel")), EnumFacing.values().toMutableSet())
    val inventoryBuffer = InventoryComponent(3, this, EnumFacing.values().toMutableSet())

    var currentRocket: EntityRocket? = null

    fun setToCurrentRocket(rocket: EntityRocket): Boolean {
        if (currentRocket == null || currentRocket!!.isDead) {
            currentRocket = rocket
            return true
        }
        return false
    }

    init {
        registerComponent(ownerShip, "ownership")
        registerComponent(fluidBuffer, "fluidBuffer")
        registerComponent(inventoryBuffer, "invBuffer")
    }
}