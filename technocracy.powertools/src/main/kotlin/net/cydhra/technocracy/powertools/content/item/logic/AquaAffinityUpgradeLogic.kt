package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.BlockBreakSpeedData
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType
import net.cydhra.technocracy.foundation.content.items.components.ItemMultiplierComponent

/**
 * Logic for the water affinity upgrade
 *
 * @param multiplier the multiplier determines how many levels of affinity should be applied
 */
class AquaAffinityUpgradeLogic(private val multiplier: ItemMultiplierComponent) : ILogic<ItemStackLogicParameters> {

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if (logicParameters.type != ItemStackTickType.BLOCK_BREAK_SPEED)
            return

        val eventData = logicParameters.data as? BlockBreakSpeedData
                ?: throw AssertionError("received block break data but got another event")

        if (eventData.event.entityPlayer.isInWater) {
            eventData.event.newSpeed = (eventData.event.newSpeed * 5f * multiplier.multiplier).toFloat()
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}