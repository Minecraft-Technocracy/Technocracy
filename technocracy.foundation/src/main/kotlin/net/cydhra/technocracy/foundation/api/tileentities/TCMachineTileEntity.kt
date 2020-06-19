package net.cydhra.technocracy.foundation.api.tileentities

import net.cydhra.technocracy.foundation.api.ecs.IAggregatableGuiProvider
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicClient
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicParameters
import net.cydhra.technocracy.foundation.api.ecs.tileentities.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.api.upgrades.Upgradable
import net.minecraft.util.ITickable

/**
 * An interface for machine tile entities. It is composed of [TCAggregatableTileEntity], [ITickable], [ILogicClient]
 * and [TCTileEntityGuiProvider] as all those must be supported by all machines.
 */
interface TCMachineTileEntity : TCAggregatableTileEntity, ITickable, ILogicClient<ILogicParameters>, TCTileEntityGuiProvider, Upgradable, IAggregatableGuiProvider