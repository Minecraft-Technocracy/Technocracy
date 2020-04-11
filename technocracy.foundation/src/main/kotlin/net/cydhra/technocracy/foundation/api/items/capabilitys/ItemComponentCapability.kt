package net.cydhra.technocracy.foundation.api.items.capabilitys

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager

object ItemComponentCapabilityHandler {

    @CapabilityInject(IComponentHandler::class)
    lateinit var COMPONENT_HANDLER: Capability<IComponentHandler?>

    fun register()
    {
        CapabilityManager.INSTANCE.register(IComponentHandler::class.java, ItemComponentCapability()) { ComponentHandlerItemStack() }
    }

    class ItemComponentCapability<T : IComponentHandler> : Capability.IStorage<T> {
        override fun readNBT(capability: Capability<T>?, instance: T, side: EnumFacing?, nbt: NBTBase?) {
            TODO("Not yet implemented")
        }

        override fun writeNBT(capability: Capability<T>?, instance: T, side: EnumFacing?): NBTBase? {
            TODO("Not yet implemented")
        }
    }
}