package net.cydhra.technocracy.foundation.conduits.types

import net.minecraftforge.energy.IEnergyStorage

/**
 * A wrapper type to abstract pipe content for the transfer algorithms
 */
abstract class PipeContent()

class PipeItemContent : PipeContent()

class PipeFluidContent : PipeContent()

class PipeEnergyContent(val source: IEnergyStorage, val amount: Int) : PipeContent()