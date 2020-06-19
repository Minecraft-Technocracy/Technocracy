package net.cydhra.technocracy.foundation.model.items.capability

import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.ecs.logic.*
import net.cydhra.technocracy.foundation.api.upgrades.Installable
import net.cydhra.technocracy.foundation.api.upgrades.UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.api.upgrades.Upgradable
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeParameter
import net.cydhra.technocracy.foundation.content.items.components.AbstractItemCapabilityComponent
import net.cydhra.technocracy.foundation.content.items.components.AbstractItemComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemMultiplierComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemOptionalAttachedComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilitySerializable


open class ItemCapabilityWrapper(var stack: ItemStack) : ICapabilitySerializable<NBTTagCompound>, Upgradable, Installable, ICapabilityWrapperCapability, ILogicClient<ItemStackLogicParameters> by LogicClientDelegate() {
    companion object {
        @JvmStatic
        @CapabilityInject(ICapabilityWrapperCapability::class)
        lateinit var CAPABILITY_WRAPPER: Capability<ICapabilityWrapperCapability>
    }

    /**
     * All machine components that are saved to NBT and possibly accessible from GUI
     */
    private val components: MutableList<Pair<String, AbstractItemComponent>> = mutableListOf()

    /**
     * All components that also offer a capability. They must also be added to [components] but for speed they are
     * also collected in this list for quick query times in [supportsCapability]
     */
    private val capabilityComponents: MutableMap<String, AbstractItemCapabilityComponent> = mutableMapOf()

    val upgradeableTypes = mutableListOf<UpgradeParameter>()

    /*init {
        components.forEach {
            it.value.wrapper = this
            if (it.value is AbstractItemCapabilityComponent) {
                capabilities[it.key] = it.value as AbstractItemCapabilityComponent
            }
        }
    }*/

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == CAPABILITY_WRAPPER) {
            return CAPABILITY_WRAPPER.cast(this)
        }
        return capabilityComponents.values
                .firstOrNull { it.hasCapability(capability, facing) }
                ?.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability == CAPABILITY_WRAPPER) return true
        return capabilityComponents.values.any { it.hasCapability(capability, facing) }
    }

    fun updateItemStack() {
        val compound = NBTTagCompound()

        for (comp in components) {
            if (comp.second.needsClientSyncing) {
                compound.setTag(comp.first, comp.second.serializeNBT())
            }
        }

        stack.setTagInfo("TC_Components", compound)
    }

    fun loadFromItemStack(stack: ItemStack) {
        val nbtComponents = stack.getOrCreateSubCompound("TC_Components")
        for (comp in components) {
            if (comp.second.needsClientSyncing && nbtComponents.hasKey(comp.first)) {
                comp.second.deserializeNBT(nbtComponents.getCompoundTag(comp.first))
                comp.second.onLoadAggregate()
            }
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        var i = 0
        if (nbt != null) {
            //we are probably on the server side and have the capability data
            while (i < components.size) {
                val (name, component) = components[i++]
                if (nbt.hasKey(name))
                    component.deserializeNBT(nbt.getCompoundTag(name))
                component.onLoadAggregate()
            }
        } else {
            //on the client side we need to load the data from the stack data
            loadFromItemStack(stack)
        }
    }

    /**
     * Called from forge
     */
    override fun serializeNBT(): NBTTagCompound {
        return serializeNBT(NBTTagCompound())
    }

    override fun canInteractWith(player: EntityPlayer?): Boolean {
        return player?.isEntityAlive ?: true
    }

    override fun getComponents(): List<Pair<String, IComponent>> {
        return components.toList()
    }

    override fun registerComponent(component: IComponent, name: String) {
        if (component is AbstractItemComponent) {
            component.wrapper = this

            components += name to component
            if (component is AbstractItemCapabilityComponent) {
                capabilityComponents[name] = component
            }

            component.onRegister()
        }
        //updateItemStack()
    }

    override fun removeComponent(name: String) {
        this.components.removeIf { (componentName, _) -> componentName == name }
        this.capabilityComponents.remove(name)
        //updateItemStack()
    }

    //not used but needs to be implemented
    override fun serializeNBT(compound: NBTTagCompound): NBTTagCompound {
        for ((name, component) in components) {
            compound.setTag(name, component.serializeNBT())
        }
        return compound
    }

    private val upgradeParameters = mutableMapOf<UpgradeParameter, ItemMultiplierComponent>()
    private val attachableParameters = mutableMapOf<UpgradeParameter, ItemOptionalAttachedComponent<out AbstractItemComponent>>()

    /**
     * Register a new upgradable parameter at the machine
     *
     * @param parameter the [UpgradeParameter] that shall be registered as supported
     * @param multiplierComponent the multiplier affected by the parameter
     */
    fun registerUpgradeParameter(
            parameter: UpgradeParameter,
            multiplierComponent: ItemMultiplierComponent) {
        this.upgradeParameters[parameter] = multiplierComponent
    }

    /**
     * Register a new upgradable parameter at the machine
     *
     * @param parameter the [UpgradeParameter] that shall be registered as supported
     * @param multiplierComponent the multiplier affected by the parameter
     */
    fun registerAttachableParameter(
            parameter: UpgradeParameter,
            optionalComponent: ItemOptionalAttachedComponent<out AbstractItemComponent>) {
        this.attachableParameters[parameter] = optionalComponent
    }

    /**
     * Apply a modification to a parameter. If this machine does not support the parameter or the parameter is
     * [UPGRADE_GENERIC], a [NullPointerException] will be thrown
     */
    override fun upgradeParameter(parameter: UpgradeParameter, modification: Double) {
        this.upgradeParameters[parameter]!!.multiplier += modification
    }

    override fun supportsParameter(parameter: UpgradeParameter): Boolean {
        return upgradeParameters.contains(parameter)
    }


    override fun supportsInstallParameter(parameter: UpgradeParameter): Boolean {
        return attachableParameters.contains(parameter) && !attachableParameters[parameter]!!.isAttached
    }

    override fun installParameter(parameter: UpgradeParameter, attach: Boolean) {
        attachableParameters[parameter]!!.isAttached = attach
    }

    fun <T : AbstractItemComponent> getAttachableParameter(parameter: UpgradeParameter): ItemOptionalAttachedComponent<T>? {
        return attachableParameters[parameter] as ItemOptionalAttachedComponent<T>?
    }
}