package net.cydhra.technocracy.foundation.data

import net.cydhra.technocracy.foundation.data.general.AbstractSaveDataElement
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import java.util.*

object OwnershipManager : AbstractSaveDataElement("ownership") {
    val ownerships = mutableListOf<Ownership>()

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        ownerships.clear()
        for (key in nbt!!.keySet) {
            val ownership = Ownership(UUID.fromString(key))
            val list = nbt.getTagList(key, Constants.NBT.TAG_COMPOUND)
            for (i in 0 until list.tagCount()) {
                val comp = list.getCompoundTagAt(i)
                ownership.users[comp.getUniqueId("uuid")!!] = Ownership.OwnershipRights.valueOf(comp.getString("rights"))
            }
            ownerships.add(ownership)
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        val list = NBTTagCompound()

        for (os in ownerships) {
            val ownership = NBTTagList()

            for (user in os.users) {
                val userTag = NBTTagCompound()

                userTag.setUniqueId("uuid", user.key)
                userTag.setString("rights", user.value.name)

                ownership.appendTag(userTag)
            }
            list.setTag(os.ownerShipUUID.toString(), ownership)
        }
        return list
    }

    fun updateUserAccess(groupID: UUID, user: UUID, userToAdd: UUID, newRight: OwnershipManager.Ownership.OwnershipRights): Boolean {
        val group = ownerships.find { it.ownerShipUUID == groupID }
        if (group != null) {
            val userRank = group.users[user]
            if (userRank != null && userRank != OwnershipManager.Ownership.OwnershipRights.ACCESS) {
                group.users[userToAdd] = OwnershipManager.Ownership.OwnershipRights.ACCESS
            }
        }
        return true
    }

    fun addUserToGroup(groupID: UUID, user: UUID, userToAdd: UUID): Boolean {
        val group = ownerships.find { it.ownerShipUUID == groupID }
        if (group != null) {
            val userRank = group.users[user]
            if (userRank != null && userRank != OwnershipManager.Ownership.OwnershipRights.ACCESS) {
                group.users[userToAdd] = OwnershipManager.Ownership.OwnershipRights.ACCESS
            }
        }
        return true
    }

    class Ownership(val ownerShipUUID: UUID) {
        val users = mutableMapOf<UUID, OwnershipRights>()

        enum class OwnershipRights {
            OWNER, MANAGE, ACCESS
        }
    }

    override fun reset() {
    }

}