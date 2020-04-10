package net.cydhra.technocracy.foundation.model.tileentities.api

import net.cydhra.technocracy.foundation.api.ecs.tileentities.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogicClient
import net.minecraft.util.ITickable

/**
 * An interface for machine tile entities. It is composed of [TCAggregatableTileEntity], [ITickable], [ILogicClient]
 * and [TCTileEntityGuiProvider] as all those must be supported by all machines.
 */
interface TCMachineTileEntity : TCAggregatableTileEntity, ITickable, ILogicClient, TCTileEntityGuiProvider