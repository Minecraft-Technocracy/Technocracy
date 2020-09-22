package net.cydhra.technocracy.powertools.content.item.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.EntityDamageData
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType
import net.cydhra.technocracy.foundation.api.ecs.logic.ItemStackTickType.Companion.ENTITY_DAMAGE
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
        if (logicParameters.data != ENTITY_DAMAGE)
            return

        val event = ENTITY_DAMAGE[logicParameters].event

        if (event.source == DamageSource.FALL) {
            // reduce by 12% for each upgrade and do not reduce to negative numbers
            event.amount -= min(
                    (event.amount * 0.12 * multiplier.multiplier).toFloat(),
                    event.amount
            )
        }
    }

    override fun postProcessing(wasProcessing: Boolean, logicParameters: ItemStackLogicParameters) {
    }
}