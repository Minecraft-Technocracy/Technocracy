package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.conduits.types.PipeType

/**
 * Any form of good that can be transferred using the conduit network. The asset is described using the pipe type
 * that transfers it as a content classification and a unique id to describe the actual item.
 *
 * @param type pipe type that transfers this asset
 * @param content unique (within [type]) id for the content.
 */
class TransferAsset(val type: PipeType, val content: Int)