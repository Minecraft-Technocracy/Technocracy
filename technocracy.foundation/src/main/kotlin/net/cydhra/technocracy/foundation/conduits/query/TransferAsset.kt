package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.conduits.transit.TransitSink
import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.WorldServer
import net.minecraftforge.items.CapabilityItemHandler

/**
 * Any form of good that can be transferred using the conduit network. The asset is described using the pipe type
 * that transfers it as a content classification and a unique id to describe the actual item. This class also handles
 * the actual transfer, as type-safety can be guaranteed this way and contents can be moved directly without cloning
 * anything, which could potentially destroy meta data.
 *
 * @param type pipe type that transfers this asset
 * @param content unique (within [type]) id for the content.
 */
abstract class TransferAsset(val type: PipeType, val content: Int) {

    /**
     * Actually perform a transfer safely. This is a strategy pattern to transfer goods between two compatible sinks.
     */
    abstract fun performTransfer(world: WorldServer, providerSink: TransitSink, target: TransitSink)

    /**
     * Test whether [target] will accept any non-zero quantity of this asset.
     */
    abstract fun acceptsAsset(world: WorldServer, target: TransitSink): Boolean
}

class ItemTransferAsset(content: Int) : TransferAsset(PipeType.ITEM, content) {
    override fun performTransfer(world: WorldServer, providerSink: TransitSink, target: TransitSink) {
        TODO("not implemented")
    }

    override fun acceptsAsset(world: WorldServer, target: TransitSink): Boolean {
        val itemHandler = world.getTileEntity(target.pos.offset(target.facing))!!
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, target.facing.opposite)!!

        val dummyStack = ItemStack(Item.getItemById(this.content))
        return (0..itemHandler.slots).firstOrNull { itemHandler.insertItem(it, dummyStack, true).isEmpty } != null
    }
}

class FluidTransferAsset(content: Int) : TransferAsset(PipeType.FLUID, content) {
    override fun performTransfer(world: WorldServer, providerSink: TransitSink, target: TransitSink) {
        TODO("not implemented")
    }

    override fun acceptsAsset(world: WorldServer, target: TransitSink): Boolean {
        TODO("not implemented")
    }
}

class EnergyTransferAsset(content: Int) : TransferAsset(PipeType.ENERGY, content) {
    override fun performTransfer(world: WorldServer, providerSink: TransitSink, target: TransitSink) {
        TODO("not implemented")
    }

    override fun acceptsAsset(world: WorldServer, target: TransitSink): Boolean {
        TODO("not implemented")
    }
}