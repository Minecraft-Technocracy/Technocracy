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

    /**
     * Drain content from the source until the amount of content available minus the drained content match the given
     * content parameter. This is used during transfer to drain the source by the amount transferred to a single target.
     *
     * @param content the amount of content remaining after draining the source
     *
     * @throws IllegalArgumentException if the given [content] parameter is a different content type
     */
    abstract fun drainSourceUntil(content: PipeContent)
}

data class PipeItemContent(val source: IItemHandler, val slot: Int, val simulatedStack: ItemStack) : PipeContent() {
    override fun isEmpty(): Boolean = simulatedStack.isEmpty

    override fun drainSourceUntil(content: PipeContent) {
        require(content is PipeItemContent) { "cannot drain item content by another content type" }
        this.source.extractItem(this.slot, simulatedStack.count - content.simulatedStack.count, false)
    }
}

data class PipeFluidContent(val source: IFluidHandler, val simulatedStack: FluidStack) : PipeContent() {
    override fun isEmpty(): Boolean = simulatedStack.amount == 0

    override fun drainSourceUntil(content: PipeContent) {
        require(content is PipeFluidContent) { "cannot drain fluid content by another content type" }
        this.source.drain(simulatedStack.amount - content.simulatedStack.amount, true)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PipeFluidContent) return false

        return super.equals(other) && this.simulatedStack.isFluidStackIdentical(other.simulatedStack)
    }
}

data class PipeEnergyContent(val source: IEnergyStorage, val amount: Int) : PipeContent() {
    override fun isEmpty(): Boolean = amount == 0

    override fun drainSourceUntil(content: PipeContent) {
        require(content is PipeEnergyContent) { "cannot drain energy content by another content type" }
        this.source.extractEnergy(this.amount - content.amount, false)
    }
}