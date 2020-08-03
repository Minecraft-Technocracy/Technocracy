package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.*
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import java.lang.Math.abs


class FireExtinguishLogic : ILogic<ItemStackLogicParameters> {

    val energyConsumtion = 5
    val damageEnergyConsumtion = 10f

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {

        val energy = logicParameters.wrapper.getEnergyComponent<ItemEnergyComponent>()?.energyStorage ?: return

        val currentEnergy = energy.currentEnergy

        if (currentEnergy < energyConsumtion) return

        if (logicParameters.type == ItemStackTickType.ENTITY_DAMAGE) {
            val event = (logicParameters.data as EntityDamageData).event
            if (event.source.isFireDamage) {

                val cost = (event.amount * damageEnergyConsumtion).toInt()
                val damage = abs(event.amount * (0.coerceAtLeast(cost - currentEnergy) / damageEnergyConsumtion))

                if (damage > 0f) {
                    event.amount = damage
                } else {
                    event.isCanceled = true
                }
                energy.consumeEnergy((event.amount * damageEnergyConsumtion).toInt())
            }
        } else if (logicParameters.type == ItemStackTickType.ENTITY_ATTACK) {
            val event = (logicParameters.data as EntityAttackData).event
            if (event.source.isFireDamage) {

                //calculate how much energy it will cost to cancel the damage
                val cost = (event.amount * damageEnergyConsumtion).toInt()
                //calculate how much of the damage can be canceled with the currently energy level
                val damage = abs(event.amount * (0.coerceAtLeast(cost - currentEnergy) / damageEnergyConsumtion))


                //only remove energy every 0.5 seconds so energy does not get drained to fast
                if (event.entityLiving.ticksExisted % 10 == 0)
                    energy.consumeEnergy((event.amount * damageEnergyConsumtion).toInt())

                //always cancel and if damage was too big reapply it
                event.isCanceled = true
                if (damage > 0f) {
                    event.entityLiving.attackEntityFrom(event.source, damage)
                }
            }
        } else if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {
            val player = logicParameters.player

            if (player.isBurning && !player.isInLava && !player.world.isFlammableWithin(player.entityBoundingBox.shrink(0.001))) {
                energy.consumeEnergy(energyConsumtion)
                player.extinguish()
            }
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}