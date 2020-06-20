package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters


class FireExtinguishLogic : ILogic<ItemStackLogicParameters> {
    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if(logicParameters.player.isBurning)
            logicParameters.player.extinguish()
        //todo energy cost?
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}