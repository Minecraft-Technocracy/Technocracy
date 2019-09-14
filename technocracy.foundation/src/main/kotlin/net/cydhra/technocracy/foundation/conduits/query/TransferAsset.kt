package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.capabilities.energy.EnergyCapabilityProvider
import net.cydhra.technocracy.foundation.conduits.transit.TransitSink
import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.WorldServer
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler

/**
 * Any form of good that can be transferred using the conduit network. The asset is described using the pipe type
 * that transfers it and any form of content type. This class also handles the actual transfer, as type-safety can be
 * guaranteed this way and no indirections must be taken to transfer different content types.
 *
 * @param type pipe type that transfers this asset
 */
abstract class TransferAsset(val type: PipeType) {

    /**
     * Actually perform a transfer safely. This is a strategy pattern to transfer goods between two compatible sinks.
     */
    abstract fun performTransfer(world: WorldServer, providerSink: TransitSink, target: TransitSink, maximumQuantity: Int)

    /**
     * Test whether [target] will accept any non-zero quantity of this asset.
     */
    abstract fun acceptsAsset(world: WorldServer, target: TransitSink): Boolean
}

class ItemTransferAsset(val content: Item) : TransferAsset(PipeType.ITEM) {
    override fun performTransfer(world: WorldServer, providerSink: TransitSink, target: TransitSink, maximumQuantity: Int) {
        val providerItemHandler = world.getTileEntity(providerSink.pos.offset(providerSink.facing))!!
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, providerSink.facing.opposite)!!
        val targetItemHandler = world.getTileEntity(target.pos.offset(target.facing))!!
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, target.facing.opposite)!!

        val providerSlot = (0..providerItemHandler.slots)
                .first { providerItemHandler.getStackInSlot(it).item == this.content }
        var transferredDummyStack = providerItemHandler.extractItem(
                providerSlot,
                maximumQuantity,
                true)
        var slotIndex = 0

        while (!transferredDummyStack.isEmpty) {
            transferredDummyStack = targetItemHandler.insertItem(slotIndex, transferredDummyStack, true)
            slotIndex++
            if (slotIndex >= targetItemHandler.slots)
                break
        }

        val quantityTransferred = maximumQuantity - transferredDummyStack.count
        check(quantityTransferred > 0) { "target does not accept anything but transfer was performed." }

        var transferredStack = providerItemHandler.extractItem(providerSlot, quantityTransferred, false)
        slotIndex = 0

        while (!transferredStack.isEmpty) {
            transferredStack = targetItemHandler.insertItem(slotIndex, transferredStack, false)
            slotIndex++
            if (slotIndex >= targetItemHandler.slots)
                throw AssertionError("insertion was impossible although it was simulated")
        }
    }

    override fun acceptsAsset(world: WorldServer, target: TransitSink): Boolean {
        val itemHandler = world.getTileEntity(target.pos.offset(target.facing))!!
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, target.facing.opposite)!!

        val dummyStack = ItemStack(this.content)
        return (0..itemHandler.slots).firstOrNull { itemHandler.insertItem(it, dummyStack, true).isEmpty } != null
    }
}

class FluidTransferAsset(val content: Fluid) : TransferAsset(PipeType.FLUID) {
    override fun performTransfer(world: WorldServer, providerSink: TransitSink, target: TransitSink, maximumQuantity: Int) {
        TODO("not implemented")
    }

    override fun acceptsAsset(world: WorldServer, target: TransitSink): Boolean {
        val fluidHandler = world.getTileEntity(target.pos.offset(target.facing))!!
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, target.facing.opposite)!!
        return fluidHandler.fill(FluidStack(this.content, 1), true) == 1
    }
}

class EnergyTransferAsset(val content: Int) : TransferAsset(PipeType.ENERGY) {
    override fun performTransfer(world: WorldServer, providerSink: TransitSink, target: TransitSink, maximumQuantity: Int) {
        TODO("not implemented")
    }

    override fun acceptsAsset(world: WorldServer, target: TransitSink): Boolean {
        val energyHandler = world.getTileEntity(target.pos.offset(target.facing))!!
                .getCapability(EnergyCapabilityProvider.CAPABILITY_ENERGY!!, target.facing.opposite)!!
        return energyHandler.canReceive() && energyHandler.receiveEnergy(1, true) == 1
    }
}