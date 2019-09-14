package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.conduits.transit.TransitSink
import net.cydhra.technocracy.foundation.conduits.types.PipeType

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
    abstract fun performTransfer(providerSink: TransitSink, target: TransitSink)

    /**
     * Test whether [target] will accept any non-zero quantity of this asset.
     */
    abstract fun acceptsAsset(target: TransitSink): Boolean
}

class ItemTransferAsset(content: Int) : TransferAsset(PipeType.ITEM, content) {
    override fun performTransfer(providerSink: TransitSink, target: TransitSink) {
        TODO("not implemented")
    }

    override fun acceptsAsset(target: TransitSink): Boolean {
        TODO("not implemented")
    }
}

class FluidTransferAsset(content: Int) : TransferAsset(PipeType.FLUID, content) {
    override fun performTransfer(providerSink: TransitSink, target: TransitSink) {
        TODO("not implemented")
    }

    override fun acceptsAsset(target: TransitSink): Boolean {
        TODO("not implemented")
    }
}

class EnergyTransferAsset(content: Int) : TransferAsset(PipeType.ENERGY, content) {
    override fun performTransfer(providerSink: TransitSink, target: TransitSink) {
        TODO("not implemented")
    }

    override fun acceptsAsset(target: TransitSink): Boolean {
        TODO("not implemented")
    }
}