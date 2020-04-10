package net.cydhra.technocracy.foundation.api.ecs.item

import net.cydhra.technocracy.foundation.api.ecs.IAggregatable
import net.cydhra.technocracy.foundation.api.ecs.tileentities.AbstractTileEntityComponent

/**
 * Aggregation of [AbstractTileEntityComponent] implementations used for itemstacks. Note, that this interface is not
 * necessarily implemented by an itemstack, but can be implemented as a delegate instead.
 */
interface TCAggregatableItemStack : IAggregatable