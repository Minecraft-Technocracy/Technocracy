package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.BlockBreakData
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType

/**
 * Logic for xp harvester upgrade.
 *
 * @param xpMultiplier the multiplier for dropped xp
 */
class XPHarvesterUpgradeLogic(private val xpMultiplier: Float) : ILogic<ItemStackLogicParameters> {
    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if (logicParameters.type != ItemStackTickType.BLOCK_BREAK)
            return

        val eventData = logicParameters.data as? BlockBreakData
                ?: throw AssertionError("received block break data but got another event")

        eventData.event.expToDrop = (eventData.event.expToDrop * this.xpMultiplier).toInt()
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}