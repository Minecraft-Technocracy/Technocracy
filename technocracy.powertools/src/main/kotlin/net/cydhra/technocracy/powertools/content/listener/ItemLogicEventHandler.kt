package net.cydhra.technocracy.powertools.content.listener

import net.cydhra.technocracy.foundation.api.ecs.logic.*
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ItemLogicEventHandler {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun equipmentEvent(event: LivingEquipmentChangeEvent) {
        val player = event.entityLiving as? EntityPlayer ?: return

        val first = (event.from.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)
        val second = (event.to.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)

        if (event.to.isEmpty || event.from.item != event.to.item) {
            first?.tick(ItemStackLogicParameters(player, EquipmentData(event.slot.slotType == EntityEquipmentSlot.Type.ARMOR, EquipmentData.EquipState.UNEQUIP)))
            second?.tick(ItemStackLogicParameters(player, EquipmentData(event.slot.slotType == EntityEquipmentSlot.Type.ARMOR, EquipmentData.EquipState.EQUIP)))
        }
    }

    @SubscribeEvent
    fun damageEvent(event: LivingDamageEvent) {
        val player = event.entityLiving as? EntityPlayer ?: return
        for ((i, stack) in player.equipmentAndArmor.withIndex()) {
            (stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)?.tick(ItemStackLogicParameters(player, EntityDamageData(event, i > 1)))
        }
    }

    @SubscribeEvent
    fun damageEvent(event: LivingAttackEvent) {
        val player = event.entityLiving as? EntityPlayer ?: return
        for ((i, stack) in player.equipmentAndArmor.withIndex()) {
            (stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)?.tick(ItemStackLogicParameters(player, EntityAttackData(event, i > 1)))
        }
    }
}