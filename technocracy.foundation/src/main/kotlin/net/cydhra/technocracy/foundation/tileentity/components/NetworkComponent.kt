package net.cydhra.technocracy.foundation.tileentity.components

import net.minecraft.nbt.NBTTagCompound
import java.util.*


class NetworkComponent : AbstractComponent() {

    override val type: ComponentType = ComponentType.NETWORK

    var uuid: UUID? = null

    override fun serializeNBT(): NBTTagCompound {
        val base = NBTTagCompound()
        if(uuid != null)
            base.setUniqueId("id", uuid!!)

        return base
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        uuid = if (nbt.hasUniqueId("id")) {
            nbt.getUniqueId("id")
        } else {
            System.err.println("PIPE HAS NO NETWORK UUID")
            UUID.randomUUID()
        }
    }
}