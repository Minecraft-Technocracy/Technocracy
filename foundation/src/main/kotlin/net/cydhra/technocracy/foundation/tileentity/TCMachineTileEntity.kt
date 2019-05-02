package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.tileentity.logic.ILogicClient
import net.minecraft.util.ITickable

interface TCMachineTileEntity : TCAggregatableTileEntity, ITickable, ILogicClient, TCTileEntityGuiProvider