package net.cydhra.technocracy.foundation.model.tileentities.api

import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogicClient
import net.minecraft.util.ITickable

interface TCMachineTileEntity : TCAggregatableTileEntity, ITickable, ILogicClient, TCTileEntityGuiProvider