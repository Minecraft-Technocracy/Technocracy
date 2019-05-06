package net.cydhra.technocracy.foundation.util

import com.google.gson.JsonObject
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

fun getFluidStack(json: JsonObject, context: JsonContext): FluidStack {
    check(json.has("fluid")) { "json is missing fluid parameter" }
    check(json.has("amount")) { "json is missing amount parameter" }

    val liquid = json.getAsJsonPrimitive("fluid").asString
    val amount = json.getAsJsonPrimitive("amount").asInt
    return FluidStack(FluidRegistry.getFluid(liquid), amount)
}