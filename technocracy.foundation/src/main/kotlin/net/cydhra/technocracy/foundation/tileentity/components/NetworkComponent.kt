package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import java.util.*


class NetworkComponent : IComponent {

    override val type: ComponentType = ComponentType.NETWORK

    var uuid: UUID? = null

    override fun serializeNBT(): NBTBase {
        val base = NBTTagCompound()
        if(uuid != null)
            base.setUniqueId("id", uuid!!)

        return base
    }

    override fun deserializeNBT(nbt: NBTBase) {
        if((nbt as NBTTagCompound).hasUniqueId("id")) {
            uuid = nbt.getUniqueId("id")
        } else {
            System.err.println("PIPE HAS NO NETWORK UUID")
            uuid = UUID.randomUUID()
        }
    }
}