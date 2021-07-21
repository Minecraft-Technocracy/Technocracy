package net.cydhra.technocracy.powertools.content.item

import com.google.common.collect.Multimap
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.items.capability.ItemCapabilityWrapper
import net.cydhra.technocracy.foundation.api.items.capability.getComponent
import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.client.gui.item.ItemUpgradesTab
import net.cydhra.technocracy.foundation.content.capabilities.energy.DynamicItemEnergyCapability
import net.cydhra.technocracy.foundation.content.items.BaseArmorItem
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemMultiplierComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemOptionalAttachedComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.content.items.upgrades.EnergyUpgrade
import net.cydhra.technocracy.foundation.content.items.util.IItemKeyBindEvent
import net.cydhra.technocracy.foundation.proxy.ClientProxy
import net.cydhra.technocracy.powertools.TCPowertools
import net.cydhra.technocracy.powertools.content.item.upgrades.UPGRADE_ARMOR_ARMOR
import net.cydhra.technocracy.powertools.content.item.upgrades.UPGRADE_ARMOR_TOUGHNESS
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraftforge.common.ISpecialArmor
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*
import kotlin.math.min


class ModularArmor(name: String, material: ArmorMaterial, slot: EntityEquipmentSlot, vararg upgradeClass: UpgradeClass) : BaseArmorItem(name, material = material, equipmentSlot = slot, renderIndex = 1), IItemKeyBindEvent, TCTileEntityGuiProvider {

    val upgradeClasses = upgradeClass.toMutableList()

    init {
        maxStackSize = 1
    }

    override fun getRGBDurabilityForDisplay(stack: ItemStack): Int {
        val energy = getComponent<ItemOptionalAttachedComponent<ItemEnergyComponent>>(stack, "battery")
        if (energy != null && energy.isAttached) {
            return 0x00FFFF33
        }

        return super.getRGBDurabilityForDisplay(stack)
    }

    override fun getMetadata(stack: ItemStack): Int {
        return getDamage(stack)
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        val energy = getComponent<ItemOptionalAttachedComponent<ItemEnergyComponent>>(stack, "battery")
        if (energy != null && energy.isAttached) {
            val storage = energy.innerComponent.energyStorage
            if (storage.capacity == 0) return 1.0
            return 1 - storage.currentEnergy / storage.capacity.toDouble()
        }
        return super.getDurabilityForDisplay(stack)
    }

    override fun setDamage(stack: ItemStack, damage: Int) {
        val cappedDmg = min(damage, getMaxDamage(stack))

        val energy = getComponent<ItemOptionalAttachedComponent<ItemEnergyComponent>>(stack, "battery")
        if (energy != null && energy.isAttached) {
            return energy.innerComponent.energyStorage.forceUpdateOfCurrentEnergy(getMaxDamage(stack) - cappedDmg)
        }

        return super.setDamage(stack, cappedDmg)
    }

    override fun isDamaged(stack: ItemStack): Boolean {
        // if false, the tooltip information about damage won't be shown
        return false
    }

    override fun getDamage(stack: ItemStack): Int {

        val energy = getComponent<ItemOptionalAttachedComponent<ItemEnergyComponent>>(stack, "battery")
        if (energy != null && energy.isAttached) {
            val storage = energy.innerComponent.energyStorage
            return storage.maxEnergyStored - storage.energyStored
        }

        return super.getDamage(stack)
    }

    override fun getMaxDamage(stack: ItemStack): Int {

        val energy = getComponent<ItemOptionalAttachedComponent<ItemEnergyComponent>>(stack, "battery")
        if (energy != null && energy.isAttached) {
            val storage = energy.innerComponent.energyStorage
            return storage.maxEnergyStored
        }

        return 400
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return true
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        val wrapper = ItemCapabilityWrapper(stack)

        wrapper.registerComponent(ItemUpgradesComponent(mutableListOf(UpgradeClass.TOOL, UpgradeClass.ARMOR).apply { addAll(upgradeClasses) }), "upgradeable")

        val battery = ItemOptionalAttachedComponent(ItemEnergyComponent(DynamicItemEnergyCapability(0, 0, -1, -1)))
        battery.innerComponent.needsClientSyncing = true
        battery.innerComponent.energyStorage.needsClientSyncing = true
        wrapper.registerComponent(battery, "battery")

        val armorMultiplier = ItemMultiplierComponent(UPGRADE_ARMOR_ARMOR, 0.0)
        val toughnessMultiplier = ItemMultiplierComponent(UPGRADE_ARMOR_TOUGHNESS, 0.0)

        wrapper.registerComponent(armorMultiplier, "armor_multiplier")
        wrapper.registerComponent(toughnessMultiplier, "toughness_multiplier")

        wrapper.registerUpgradeParameter(UPGRADE_ARMOR_ARMOR, armorMultiplier)
        wrapper.registerUpgradeParameter(UPGRADE_ARMOR_TOUGHNESS, toughnessMultiplier)
        wrapper.registerAttachableParameter(EnergyUpgrade.INSTALL_ENERGY, battery)

        wrapper.energyComponentProvider = { if (battery.isAttached) battery.innerComponent else null }

        return wrapper
    }

    override fun damageArmor(entity: EntityLivingBase?, stack: ItemStack, source: DamageSource?, damage: Int, slot: Int) {

    }

    override fun getProperties(player: EntityLivingBase?, armor: ItemStack, source: DamageSource?, damage: Double, slot: Int): ISpecialArmor.ArmorProperties {
        val prop = ISpecialArmor.ArmorProperties(0, 1.0, Int.MAX_VALUE)
        prop.Armor = getComponent<ItemMultiplierComponent>(armor, "armor_multiplier")?.multiplier ?: 0.0
        prop.Toughness = getComponent<ItemMultiplierComponent>(armor, "toughness_multiplier")?.multiplier ?: 0.0
        return prop
    }

    override fun getArmorDisplay(player: EntityPlayer?, armor: ItemStack, slot: Int): Int {
        return ((getComponent<ItemMultiplierComponent>(armor, "armor_multiplier")?.multiplier ?: 0.0) * 2).toInt()
    }

    override fun getAttributeModifiers(slot: EntityEquipmentSlot, stack: ItemStack): Multimap<String, AttributeModifier> {
        val map = super.getAttributeModifiers(slot, stack)

        if (slot == armorType) {
            map.put(SharedMonsterAttributes.ARMOR.name, AttributeModifier(ARMOR_MODIFIERS[armorType.index], "Armor modifier", getComponent<ItemMultiplierComponent>(stack, "armor_multiplier")?.multiplier
                    ?: 0.0, 0))
            map.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.name, AttributeModifier(ARMOR_MODIFIERS[armorType.index], "Armor toughness", getComponent<ItemMultiplierComponent>(stack, "toughness_multiplier")?.multiplier
                    ?: 0.0, 0))
        }

        return map
    }

    @SideOnly(Side.CLIENT)
    override fun getKeyBind(): KeyBinding {
        return ClientProxy.instance.itemUpgradeGui
    }

    override fun keyPress(player: EntityPlayer, itemStack: ItemStack) {
        if (!player.world.isRemote) {
            player.openGui(TCFoundation, TCGuiHandler.itemGui, player.world, player.posX.toInt(), player.posY.toInt(), player.posY.toInt())
        }
    }

    override fun getGui(player: EntityPlayer?, other: TCGui?): TCGui {
        val stack = player!!.heldItemMainhand
        val wrapped = stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null)!!
        val gui = other ?: SimpleGui(container = TCContainer(wrapped), guiHeight = 206)

        val upgradesComponent = wrapped.getComponents().firstOrNull { (_, c) -> c is ItemUpgradesComponent }?.second
        if (upgradesComponent != null) {
            gui.registerTab(ItemUpgradesTab(gui, upgradesComponent as ItemUpgradesComponent, player))
        }

        //lock the current stack so it cant be moved
        gui.container.lockItem(stack)

        gui.container.fixItemsNotSyncingBecauseOfShittyNetworkDesign = true

        return gui
    }

    companion object {

        val ARMOR_MODIFIERS = arrayOf(UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"))
        val armor = EnumHelper.addArmorMaterial("${TCPowertools.MODID}:modular_armor", "${TCPowertools.MODID}:modular_armor", 0, /*intArrayOf(3, 6, 8, 3)*/intArrayOf(0, 0, 0, 0), 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0f)!!
    }

}