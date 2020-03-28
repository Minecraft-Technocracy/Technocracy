package net.cydhra.technocracy.foundation.conduits.types

import net.minecraft.item.ItemStack
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler

/**
 * A wrapper type to abstract pipe content for the transfer algorithms
 */
abstract class PipeContent() {
    /**
     * Whether the pipe content package has a content size of 0. It will have a content type, but no amount of its
     * content.
     */
    abstract fun isEmpty(): Boolean
}

data class PipeItemContent(val source: IItemHandler, val simulatedStack: ItemStack) : PipeContent() {
    override fun isEmpty(): Boolean = simulatedStack.isEmpty
}

data class PipeFluidContent(val source: IFluidHandler, val simulatedStack: FluidStack) : PipeContent() {
    override fun isEmpty(): Boolean = simulatedStack.amount == 0
}

data class PipeEnergyContent(val source: IEnergyStorage, val amount: Int) : PipeContent() {
    override fun isEmpty(): Boolean = amount == 0
}