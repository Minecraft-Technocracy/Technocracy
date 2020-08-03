package net.cydhra.technocracy.powertools.content.listener

import net.cydhra.technocracy.foundation.api.ecs.logic.EntityAttackData
import net.cydhra.technocracy.foundation.api.ecs.logic.EntityDamageData
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ItemLogicEventHandler {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun damageEvent(event: LivingDamageEvent) {
        val player = event.entityLiving as? EntityPlayer ?: return

        for (stack in player.equipmentAndArmor) {
            (stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)?.tick(ItemStackLogicParameters(player, EntityDamageData(event)))
        }
    }

    @SubscribeEvent
    fun damageEvent(event: LivingAttackEvent) {
        val player = event.entityLiving as? EntityPlayer ?: return

        for (stack in player.equipmentAndArmor) {
            (stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null) as? ItemCapabilityWrapper)?.tick(ItemStackLogicParameters(player, EntityAttackData(event)))
        }
    }
}