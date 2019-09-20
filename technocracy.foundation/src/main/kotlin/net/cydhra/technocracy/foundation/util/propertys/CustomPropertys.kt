package net.cydhra.technocracy.foundation.util.propertys

import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.FluidStack


val POSITION = UnlistedProperty("position", BlockPos::class.java)
val FLUIDSTACK = UnlistedProperty("fluid_stack", FluidStack::class.java)