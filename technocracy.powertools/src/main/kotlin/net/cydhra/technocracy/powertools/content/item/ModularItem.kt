package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.client.gui.item.ItemUpgradesTab
import net.cydhra.technocracy.foundation.content.capabilities.energy.DynamicItemEnergyCapability
import net.cydhra.technocracy.foundation.content.items.components.ItemBatteryAddonComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemOptionalAttachedComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.content.items.upgrades.EnergyUpgrade.Companion.INSTALL_ENERGY
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import kotlin.math.min


class ModularItem : BaseItem("modularitem"), TCTileEntityGuiProvider {

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        val wrapper = ItemCapabilityWrapper(stack)
        val battery = ItemOptionalAttachedComponent(ItemEnergyComponent(DynamicItemEnergyCapability(0, 0, -1, -1)))
        battery.innerComponent.needsClientSyncing = true
        battery.innerComponent.energyStorage.needsClientSyncing = true

        wrapper.registerComponent(battery, "battery")
        wrapper.registerAttachableParameter(INSTALL_ENERGY, battery)
        wrapper.registerComponent(ItemUpgradesComponent(3, UpgradeClass.TOOL), "upgradeable")
        return wrapper
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    override fun hitEntity(stack: ItemStack, target: EntityLivingBase?, attacker: EntityLivingBase?): Boolean {
        stack.damageItem(2, attacker)
        return true
    }

    /**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    override fun onBlockDestroyed(stack: ItemStack, worldIn: World, state: IBlockState, pos: BlockPos?, entityLiving: EntityLivingBase?): Boolean {
        if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos).toDouble() != 0.0) {
            stack.damageItem(1, entityLiving)
        }
        return true
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

    inline fun <reified T : IComponent> getComponent(stack: ItemStack, name: String, side: EnumFacing? = null): T? {
        val wrapped = stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, side)
        if (wrapped != null) {
            return wrapped.getComponents().find { it.first == name }?.second as T
        }
        return null
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

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        // TODO localize, don't call it "damage" and don't print as double
        tooltip.add("Current Damage: " + getDamage(stack))
        super.addInformation(stack, worldIn, tooltip, flagIn)
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

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        if (!playerIn.isSneaking) {
            if (!worldIn.isRemote && handIn == EnumHand.MAIN_HAND) {
                playerIn.openGui(TCFoundation, TCGuiHandler.itemGui, worldIn, playerIn.posX.toInt(), playerIn.posY.toInt(), playerIn.posY.toInt())
            }
        }
        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val stack = player!!.heldItemMainhand
        val wrapped = stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null)!!
        val gui = SimpleGui(container = TCContainer(wrapped))

        val upgradesComponent = wrapped.getComponents().firstOrNull { (_, c) -> c is ItemUpgradesComponent }?.second
        if (upgradesComponent != null) {
            gui.registerTab(ItemUpgradesTab(gui, upgradesComponent as ItemUpgradesComponent, player))
        }

        //lock the current stack so it cant be moved
        gui.container.lockItem(stack)

        return gui
    }
}