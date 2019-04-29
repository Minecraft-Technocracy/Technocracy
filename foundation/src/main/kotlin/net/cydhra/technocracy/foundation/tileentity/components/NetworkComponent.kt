package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import java.util.*


class NetworkComponent : IComponent {

    var uuid: UUID? = null

    override fun serializeNBT(): NBTBase {
        val base = NBTTagCompound()
        if(uuid != null)
            base.setUniqueId("networkId", uuid)
        return base
    }

    override fun deserializeNBT(nbt: NBTBase) {
        if((nbt as NBTTagCompound).hasKey("networkId")) {
            uuid = nbt.getUniqueId("networkId")
        }
    }
}