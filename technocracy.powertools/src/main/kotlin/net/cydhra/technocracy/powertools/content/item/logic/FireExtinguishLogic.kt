package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.*
import net.cydhra.technocracy.foundation.content.items.components.ItemEnergyComponent
import net.minecraftforge.fml.relauncher.Side
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

        val serverside = logicParameters.side == Side.SERVER

        when (logicParameters.type) {
            ItemStackTickType.ENTITY_DAMAGE -> {

                val event = (logicParameters.data as EntityDamageData).event
                if (event.source.isFireDamage) {

                    val cost = (event.amount * damageEnergyConsumtion).toInt()
                    val damage = abs(event.amount * (0.coerceAtLeast(cost - currentEnergy) / damageEnergyConsumtion))

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
                        energy.consumeEnergy((event.amount * damageEnergyConsumtion).toInt())
                }
            }

            ItemStackTickType.ENTITY_ATTACK -> {
                val event = (logicParameters.data as EntityAttackData).event
                if (event.source.isFireDamage) {

                    //calculate how much energy it will cost to cancel the damage
                    val cost = (event.amount * damageEnergyConsumtion).toInt()
                    //calculate how much of the damage can be canceled with the currently energy level
                    val damage = abs(event.amount * (0.coerceAtLeast(cost - currentEnergy) / damageEnergyConsumtion))

                    val entity = event.entityLiving

                    //todo maybe add someday, needs AT of lastDamage field for now just count dmg if hurtime is zero
                    //val hurtResistanceFlag = entity.hurtResistantTime > entity.maxHurtResistantTime / 2f
                    //val damageFlag = damage <= entity.lastDamage
                    val hurtTimeFlag = entity.hurtResistantTime == 0

                    if (serverside && hurtTimeFlag)
                        energy.consumeEnergy((event.amount * damageEnergyConsumtion).toInt())

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

            ItemStackTickType.ARMOR_TICK -> {
                val player = logicParameters.player

                if (player.isBurning && !player.isInLava && !player.world.isFlammableWithin(player.entityBoundingBox.shrink(0.001))) {

                    if (serverside)
                        energy.consumeEnergy(energyConsumtion)

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