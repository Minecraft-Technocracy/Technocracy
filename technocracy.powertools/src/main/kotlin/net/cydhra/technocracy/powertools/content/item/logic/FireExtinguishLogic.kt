package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.*
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType.Companion.ARMOR_TICK
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType.Companion.ENTITY_ATTACK
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType.Companion.ENTITY_DAMAGE
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.minecraftforge.fml.relauncher.Side
import java.lang.Math.abs


class FireExtinguishLogic : ILogic<ItemStackLogicParameters> {

    val energyConsumption = 5
    val damageEnergyConsumption = 10f

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {

        val energy = logicParameters.wrapper.getEnergyComponent<ItemEnergyComponent>()?.energyStorage ?: return
        val currentEnergy = energy.currentEnergy

        val serverside = logicParameters.side == Side.SERVER

        when (logicParameters.data) {
            ENTITY_DAMAGE -> {
                val event = ENTITY_DAMAGE[logicParameters].event
                if (event.source.isFireDamage) {

                    val cost = (event.amount * damageEnergyConsumption).toInt()
                    val damage = abs(event.amount * (0.coerceAtLeast(cost - currentEnergy) / damageEnergyConsumption))

                    val hurtTimeFlag = event.entityLiving.hurtResistantTime == 0

                    if (damage > 0f) {
                        event.amount = damage
                    } else {
                        event.isCanceled = true
                        //set hurt time of entity so damage cooldown is applied
                        if (hurtTimeFlag)
                            event.entityLiving.hurtResistantTime = event.entityLiving.maxHurtResistantTime
                    }

                    if (serverside && hurtTimeFlag)
                        energy.consumeEnergy((event.amount * damageEnergyConsumption).toInt())
                }
            }

            ENTITY_ATTACK -> {
                val event = ENTITY_ATTACK[logicParameters].event
                if (event.source.isFireDamage) {

                    //calculate how much energy it will cost to cancel the damage
                    val cost = (event.amount * damageEnergyConsumption).toInt()
                    //calculate how much of the damage can be canceled with the currently energy level
                    val damage = abs(event.amount * (0.coerceAtLeast(cost - currentEnergy) / damageEnergyConsumption))

                    val entity = event.entityLiving

                    //todo maybe add someday, needs AT of lastDamage field for now just count dmg if hurtime is zero
                    //val hurtResistanceFlag = entity.hurtResistantTime > entity.maxHurtResistantTime / 2f
                    //val damageFlag = damage <= entity.lastDamage
                    val hurtTimeFlag = entity.hurtResistantTime == 0

                    if (serverside && hurtTimeFlag)
                        energy.consumeEnergy((event.amount * damageEnergyConsumption).toInt())

                    //always cancel and if damage was too big reapply it
                    event.isCanceled = true

                    if (damage > 0f) {
                        entity.attackEntityFrom(event.source, damage)
                    } else {
                        //set hurt time of entity so damage cooldown is applied
                        if (hurtTimeFlag)
                            entity.hurtResistantTime = entity.maxHurtResistantTime
                    }
                }
            }

            ARMOR_TICK -> {
                val player = logicParameters.player

                if (player.isBurning && !player.isInLava && !player.world.isFlammableWithin(player.entityBoundingBox.shrink(0.001))) {

                    if (serverside)
                        energy.consumeEnergy(energyConsumption)

                    player.extinguish()
                }
            }
            else -> {
            }
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}