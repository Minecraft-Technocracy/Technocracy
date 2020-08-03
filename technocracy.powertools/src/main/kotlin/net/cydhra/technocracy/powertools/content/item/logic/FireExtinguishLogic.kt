package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.*


class FireExtinguishLogic : ILogic<ItemStackLogicParameters> {
    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if (logicParameters.type == ItemStackTickType.ENTITY_DAMAGE) {
            val event = (logicParameters.data as EntityDamageData).event
            if (event.source.isFireDamage)
                event.isCanceled = true
        } else if (logicParameters.type == ItemStackTickType.ENTITY_ATTACK) {
            val event = (logicParameters.data as EntityAttackData).event
            if (event.source.isFireDamage)
                event.isCanceled = true
        } else if (logicParameters.type == ItemStackTickType.ARMOR_TICK) {
            if (logicParameters.player.isBurning) {
                logicParameters.player.extinguish()
            }
            //todo energy cost?
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}