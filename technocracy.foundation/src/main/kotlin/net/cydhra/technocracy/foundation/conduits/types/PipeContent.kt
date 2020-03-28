package net.cydhra.technocracy.foundation.conduits.types

import net.minecraft.item.ItemStack
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler

/**
 * A wrapper type to abstract pipe content for the transfer algorithms
 */
abstract class PipeContent()

data class PipeItemContent(val source: IItemHandler, val simulatedStack: ItemStack) : PipeContent()

data class PipeFluidContent(val source: IFluidHandler, val simulatedStack: FluidStack) : PipeContent()

data class PipeEnergyContent(val source: IEnergyStorage, val amount: Int) : PipeContent()