package net.cydhra.technocracy.foundation.tileentity.api

/**
 * A tile entity that is aggregatable itself. The difference to [TCAggregatable] is, that [TCAggregatable] can be
 * implemented independent from a tile entity (e.g. as a delegate). Therefore this interface does not declare
 * additional members
 */
interface TCAggregatableTileEntity : TCTileEntity, TCAggregatable