package net.cydhra.technocracy.foundation.util.propertys

import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.FluidStack
import javax.vecmath.Vector3f


val POSITION = UnlistedProperty("position", BlockPos::class.java)
val FLUIDSTACK = UnlistedProperty("fluid_stack", FluidStack::class.java)
val DIMENSIONS = UnlistedProperty("dimensions", Vector3f::class.java)
val TANKSIZE = UnlistedProperty("tanksize", Integer::class.java)