package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.EntityDamageData
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType
import net.cydhra.technocracy.foundation.content.items.components.ItemMultiplierComponent
import net.minecraft.util.DamageSource
import kotlin.math.min

/**
 * Logic for the anti fall damage upgrade
 *
 * @param multiplier the multiplier determines how many levels of feather fall should be applied
 */
class NoFallLogic(private val multiplier: ItemMultiplierComponent) : ILogic<ItemStackLogicParameters> {

    override fun preProcessing(logicParameters: ItemStackLogicParameters): Boolean {
        return true
    }

    override fun processing(logicParameters: ItemStackLogicParameters) {
        if (logicParameters.type != ItemStackTickType.ENTITY_DAMAGE)
            return

        val eventData = logicParameters.data as? EntityDamageData
                ?: throw AssertionError("received block break data but got another event")

        if (eventData.event.source == DamageSource.FALL) {
            // reduce by 12% for each upgrade and do not reduce to negative numbers
            eventData.event.amount -= min(
                    (eventData.event.amount * 0.12 * multiplier.multiplier).toFloat(),
                    eventData.event.amount
            )
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}