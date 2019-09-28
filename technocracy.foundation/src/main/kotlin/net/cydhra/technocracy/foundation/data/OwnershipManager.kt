package net.cydhra.technocracy.foundation.data

import net.cydhra.technocracy.foundation.data.general.AbstractSaveDataElement
import net.cydhra.technocracy.foundation.data.general.DataManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import java.util.*

object OwnershipManager : AbstractSaveDataElement("ownership") {
    val groups = mutableListOf<Ownership>()
    val playerPriories = mutableMapOf<UUID, MutableList<UUID>>()

    @Suppress("NAME_SHADOWING")
    override fun deserializeNBT(nbt: NBTTagCompound) {
        groups.clear()
        playerPriories.clear()

        val groups = nbt.getCompoundTag("groups")
        val priorities = nbt.getTagList("userPriority", Constants.NBT.TAG_COMPOUND)

        for (key in groups.keySet) {
            val ownership = Ownership(UUID.fromString(key))
            val list = nbt.getTagList(key, Constants.NBT.TAG_COMPOUND)
            for (i in 0 until list.tagCount()) {
                val comp = list.getCompoundTagAt(i)
                ownership.users[comp.getUniqueId("uuid")!!] = Ownership.OwnershipRights.valueOf(comp.getString("rights"))
            }
            this.groups.add(ownership)
        }

        for (id in 0 until priorities.tagCount()) {
            val playerPriority = priorities.getCompoundTagAt(id)
            val playerUUID = playerPriority.getUniqueId("uuid")!!
            val order = playerPriority.getTagList("order", Constants.NBT.TAG_COMPOUND)
            val orderList = mutableListOf<UUID>()
            for (id in 0 until order.tagCount()) {
                val playerPriority = priorities.getCompoundTagAt(id)
                orderList.add(playerPriority.getUniqueId("uuid")!!)
            }
            orderList.reverse()
            playerPriories[playerUUID] = orderList
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        val list = NBTTagCompound()
        val groups = NBTTagCompound()
        val priorities = NBTTagList()

        for (os in this.groups) {
            val ownership = NBTTagList()

            for (user in os.users) {
                val userTag = NBTTagCompound()

                userTag.setUniqueId("uuid", user.key)
                userTag.setString("rights", user.value.name)

                ownership.appendTag(userTag)
            }
            groups.setTag(os.ownerShipUUID.toString(), ownership)
        }

        for (pair in playerPriories) {
            val playerPrioTag = NBTTagCompound()
            val order = NBTTagList()

            for (groupOrder in pair.value) {
                val inner = NBTTagCompound()
                inner.setUniqueId("uuid", groupOrder)
                order.appendTag(inner)
            }

            playerPrioTag.setUniqueId("uuid", pair.key)
            playerPrioTag.setTag("order", order)
            priorities.appendTag(playerPrioTag)
        }

        list.setTag("groups", groups)
        list.setTag("userPriority", priorities)
        return list
    }

    fun getUserGroup(user: UUID): Ownership {
        return getGroup(playerPriories.getOrPut(user) { mutableListOf() }.getOrElse(0) {
            val id = UUID.randomUUID()
            val group = Ownership(id)
            group.users[user] = OwnershipManager.Ownership.OwnershipRights.OWNER
            groups.add(group)
            playerPriories[user]!!.add(id)
            DataManager.manager!!.markDirty()
            id
        })!!
    }

    fun getGroup(groupID: UUID): Ownership? {
        return groups.find { it.ownerShipUUID == groupID }
    }

    fun updateUserAccess(groupID: UUID, user: UUID, userToAdd: UUID, newRight: Ownership.OwnershipRights): Boolean {
        val group = groups.find { it.ownerShipUUID == groupID }
        if (group != null) {
            val userRank = group.users[user]
            if (userRank != null && userRank != OwnershipManager.Ownership.OwnershipRights.ACCESS) {
                group.users[userToAdd] = newRight
                DataManager.manager!!.markDirty()
                return true
            }
        }
        return false
    }

    fun addUserToGroup(groupID: UUID, user: UUID, userToAdd: UUID): Boolean {
        val group = groups.find { it.ownerShipUUID == groupID }
        if (group != null) {
            val userRank = group.users[user]
            if (userRank != null && userRank != OwnershipManager.Ownership.OwnershipRights.ACCESS) {
                group.users[userToAdd] = OwnershipManager.Ownership.OwnershipRights.ACCESS
                DataManager.manager!!.markDirty()
                return true
            }
        }
        return false
    }

    class Ownership(val ownerShipUUID: UUID) {
        val users = mutableMapOf<UUID, OwnershipRights>()

        fun getRights(user: UUID): OwnershipRights {
            return users[user]!!
        }

        enum class OwnershipRights {
            OWNER, MANAGE, ACCESS
        }
    }

    override fun reset() {
        groups.clear()
        playerPriories.clear()
    }

}