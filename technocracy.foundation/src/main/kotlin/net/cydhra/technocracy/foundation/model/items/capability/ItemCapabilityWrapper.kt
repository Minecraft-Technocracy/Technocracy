package net.cydhra.technocracy.foundation.model.items.capability

import net.cydhra.technocracy.foundation.api.ecs.IAggregatable
import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.upgrades.Upgradable
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.content.blocks.capacitorControllerBlock
import net.cydhra.technocracy.foundation.content.items.components.AbstractItemComponent
import net.cydhra.technocracy.foundation.util.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable


class ItemCapabilityWrapper(var stack: ItemStack, val components: MutableMap<String, AbstractItemComponent>) : ICapabilitySerializable<NBTTagCompound>, Upgradable, IAggregatable {
    val capabilities = mutableMapOf<String, AbstractItemCapabilityComponent>()

    val upgradeableTypes = mutableListOf<UpgradeParameter>()

    init {
        components.forEach {
            it.value.wrapper = this
            if (it.value is AbstractItemCapabilityComponent) {
                capabilities[it.key] = it.value as AbstractItemCapabilityComponent
            }
        }
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        val option = capabilities.values.stream().filter { it.hasCapability(capability, facing) }.findFirst()
        if (option.isPresent)
            return option.get().getCapability(capability, facing)

        return null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        for (cap in capabilities.values) {
            if (cap.hasCapability(capability, facing))
                return true
        }
        return false
    }

    fun getCombinedNBT(): NBTTagCompound {
        val wrapped = NBTTagCompound()
        for ((k, v) in components) {
            wrapped.setTag(k, v.serializeNBT())
        }
        return wrapped
    }

    fun updateItemStack() {
        if (stack.tagCompound == null)
            stack.tagCompound = NBTTagCompound()

        val nbtComponents = NBTTagCompound()

        for (comp in components) {
            if (comp.value.needsClientSyncing) {
                nbtComponents.setTag(comp.key, comp.value.serializeNBT())
            }
        }

        stack.tagCompound?.setTag("TC_Components", nbtComponents)
    }

    fun loadFromItemStack(stack: ItemStack) {
        val nbt = stack.tagCompound ?: return
        val nbtComponents = nbt.getCompoundTag("TC_Components")
        for (comp in components) {
            if (comp.value.needsClientSyncing) {
                comp.value.deserializeNBT(nbtComponents[comp.key])
            }
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        if (nbt != null) {
            components.forEach {
                it.value.deserializeNBT(nbt.getCompoundTag(it.key))
            }
        }
        loadFromItemStack(stack)
    }

    override fun serializeNBT(): NBTTagCompound {
        return getCombinedNBT()
    }

    override fun supportsParameter(parameter: UpgradeParameter): Boolean {
        return upgradeableTypes.contains(parameter)
    }

    override fun upgradeParameter(parameter: UpgradeParameter, modification: Double) {
        TODO("Not yet implemented")
    }

    override fun getComponents(): List<Pair<String, IComponent>> {
        return components.toList()
    }

    override fun registerComponent(component: IComponent, name: String) {
        if(component is AbstractItemComponent) {
            components[name] = component
            if(component is AbstractItemCapabilityComponent) {
                capabilities[name] = component
            }
        }
    }

    override fun removeComponent(name: String) {
        components.remove(name)
    }

    //not used but needs to be implemented
    override fun serializeNBT(compound: NBTTagCompound): NBTTagCompound {
        return getCombinedNBT()
    }

}