package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.coremod.event.ItemCooldownEvent
import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.content.capabilities.energy.DynamicItemEnergyCapability
import net.cydhra.technocracy.foundation.content.items.components.AbstractItemComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.min


class EnergyShieldItem : BaseItem("energy_shield") {

    init {
        addPropertyOverride(ResourceLocation("blocking")) { stack, _, entityIn ->
            if (entityIn != null && entityIn.isHandActive && entityIn.activeItemStack == stack) {
                1.0f
            } else {
                0.0f
            }
        }
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        val wrapper = ItemCapabilityWrapper(stack)
        val energy = ItemEnergyComponent(DynamicItemEnergyCapability(0, 16000, 0))
        wrapper.registerComponent(energy, "energy")

        energy.needsClientSyncing = true
        energy.energyStorage.needsClientSyncing = true

        return wrapper
    }

    override fun getRGBDurabilityForDisplay(stack: ItemStack): Int {
        return 0x00FFFF33
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        val energy = getComponent<ItemEnergyComponent>(stack, "energy")
        if (energy != null) {
            val storage = energy.energyStorage
            if (storage.capacity == 0) return 1.0
            return 1 - storage.currentEnergy / storage.capacity.toDouble()
        }
        return super.getDurabilityForDisplay(stack)
    }

    //important or else if used in creative it sets the damage wrong
    override fun getMetadata(stack: ItemStack): Int {
        return getDamage(stack)
    }

    override fun setDamage(stack: ItemStack, damage: Int) {
        val cappedDmg = min(damage, getMaxDamage(stack))

        val energy = getComponent<ItemEnergyComponent>(stack, "energy")
        if (energy != null)
            return energy.energyStorage.forceUpdateOfCurrentEnergy(getMaxDamage(stack) - cappedDmg)
    }

    override fun getDamage(stack: ItemStack): Int {
        val energy = getComponent<ItemEnergyComponent>(stack, "energy")
        if (energy != null)
            return energy.energyStorage.maxEnergyStored - energy.energyStorage.energyStored
        return super.getDamage(stack)
    }

    override fun getMaxDamage(stack: ItemStack): Int {
        val energy = getComponent<ItemEnergyComponent>(stack, "energy")
        if (energy != null)
            return energy.energyStorage.maxEnergyStored
        return 400
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return true
    }

    override fun isDamaged(stack: ItemStack): Boolean {
        // if false, the tooltip information about damage won't be shown
        return false
    }

    @SubscribeEvent
    fun onDamage(event: ItemCooldownEvent) {
        if (event.item == this) {
            event.delay = 50
        }
    }

    @SubscribeEvent
    fun onDamage(event: LivingAttackEvent) {
        val entity = event.entity
        //entity is using this shield
        val attacker = event.source.immediateSource
        if (entity is EntityLivingBase && attacker is EntityLivingBase &&
                entity.isHandActive && entity.activeItemStack.item == this &&
                !attacker.heldItemMainhand.item.canDisableShield(attacker.heldItemMainhand, entity.activeItemStack, attacker, entity)) {
            attacker.attackEntityFrom(DamageSource.causeThornsDamage(attacker), event.amount * 0.5f)
        }
    }

    override fun getMaxItemUseDuration(stack: ItemStack?): Int {
        return 72000
    }

    override fun isShield(stack: ItemStack, entity: EntityLivingBase?): Boolean {
        return true
    }

    override fun onItemRightClick(worldIn: World?, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val energy = getComponent<ItemEnergyComponent>(playerIn.getHeldItem(hand), "energy")
        if (energy != null) {
            if (energy.energyStorage.currentEnergy <= 0)
                return ActionResult(EnumActionResult.FAIL, playerIn.getHeldItem(hand))
        }

        playerIn.activeHand = hand
        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand))
    }

    override fun getItemUseAction(stack: ItemStack?): EnumAction? {
        return EnumAction.BLOCK
    }

    inline fun <reified T : IComponent> getComponent(stack: ItemStack, name: String, side: EnumFacing? = null): T? {
        val wrapped = stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, side)
        if (wrapped != null) {
            return wrapped.getComponents().find { it.first == name }?.second as T
        }
        return null
    }
}