package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.BlockBreakSpeedData
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType

/**
 * Logic for the water affinity upgrade
 */
class AquaAffinityUpgradeLogic(private val level: Int = 1) : ILogic<ItemStackLogicParameters> {

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if (logicParameters.type != ItemStackTickType.BLOCK_BREAK_SPEED)
            return

        val eventData = logicParameters.data as? BlockBreakSpeedData
                ?: throw AssertionError("received block break data but got another event")

        if (eventData.event.entityPlayer.isInWater && !eventData.event.entityPlayer.onGround) {
            eventData.event.newSpeed = eventData.event.newSpeed + 5 * level
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}