package net.cydhra.technocracy.foundation.pipes.types

import net.cydhra.technocracy.foundation.capabilities.energy.EnergyCapabilityProvider
import net.cydhra.technocracy.foundation.tileentity.TileEntityPipe
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler


enum class PipeType(val unlocalizedName: String, val capability: Capability<*>?, val splitInputEqual: Boolean) :
        IStringSerializable {
    ENERGY("energy", EnergyCapabilityProvider.CAPABILITY_ENERGY, true),
    FLUID("fluid", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, true),
    ITEM("item", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, false);

    fun canDoAction(current: PipeType, pipeIn: TileEntityPipe, pipeOut: TileEntityPipe, tileIn: TileEntity,
            tileOut: TileEntity, facingIn: EnumFacing, facingOut: EnumFacing): Boolean {

        val capIn = tileIn.getCapability(current.capability!!, facingIn.opposite)
        val capOut = tileOut.getCapability(current.capability, facingOut.opposite)

        //todo auslagern zu den enums
        if (current == ITEM) {
            val handlerIn = capIn as IItemHandler
            val handlerOut = capOut as IItemHandler

            inputSlots@ for (slotIn in 0 until handlerIn.slots) {
                //todo limit item transfer rate
                //todo item inout/output filtering
                var limit = 8
                var stackIn = handlerIn.extractItem(slotIn, limit, true)
                if (stackIn != ItemStack.EMPTY) {
                    for (slotOut in 0 until handlerOut.slots) {
                        val stackOut = handlerOut.insertItem(slotOut, stackIn, true)
                        if (stackOut.isEmpty || stackIn.count != stackOut.count) {
                            if (stackOut.isEmpty) {
                                stackIn = handlerIn.extractItem(slotIn, limit, false)
                                handlerOut.insertItem(slotOut, stackIn, false)
                            } else {
                                limit = Math.min(limit, stackOut.count)
                                stackIn = handlerIn.extractItem(slotIn, limit, false)
                                handlerOut.insertItem(slotOut, stackIn, false)
                            }
                            return true
                        }
                    }

                }
            }

            return false
        }
        println(current)
        TODO("not implemented")
    }

    override fun getName(): String {
        return this.unlocalizedName
    }
}