@file:Suppress("unused")

package net.cydhra.technocracy.powertools.content.listener

import net.cydhra.technocracy.foundation.api.ecs.logic.*
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ItemLogicEventHandler {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onTick(event: LivingEvent.LivingUpdateEvent) {
        val player = event.entityLiving
        if (player !is EntityPlayer) return

        val tickables = mutableListOf<ItemCapabilityWrapper>()

        for (item in event.entityLiving.armorInventoryList) {  // FORGE: Tick armor on animation ticks
            if (!item.isEmpty) {
                val cap = item.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper
                        ?: continue
                tickables.add(cap)
            }
        }

        for (prio in ItemStackTickPriority.values()) {
            for (cap in tickables) {
                cap.tick(ItemStackLogicParameters(player, EntityArmorTickData(prio)))
            }
        }
    }

    @SubscribeEvent
    fun equipmentEvent(event: LivingEquipmentChangeEvent) {
        val player = event.entityLiving as? EntityPlayer ?: return

        val first = (event.from.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)
        val second = (event.to.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)

        first?.tick(ItemStackLogicParameters(player, EquipmentData(event.from, event.to, event.slot.slotType == EntityEquipmentSlot.Type.ARMOR, EquipmentData.EquipState.UNEQUIP)))
        second?.tick(ItemStackLogicParameters(player, EquipmentData(event.from, event.to, event.slot.slotType == EntityEquipmentSlot.Type.ARMOR, EquipmentData.EquipState.EQUIP)))
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

    @SubscribeEvent
    fun breakBlockEvent(event: BlockEvent.BreakEvent) {
        for ((_, stack) in event.player.equipmentAndArmor.withIndex()) {
            (stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)
                    ?.tick(ItemStackLogicParameters(event.player, BlockBreakData(event)))
        }
    }

    @SubscribeEvent
    fun breakSpeedEvent(event: PlayerEvent.BreakSpeed) {
        for ((_, stack) in event.entityPlayer.equipmentAndArmor.withIndex()) {
            (stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)
                    ?.tick(ItemStackLogicParameters(event.entityPlayer, BlockBreakSpeedData(event)))
        }
    }
}