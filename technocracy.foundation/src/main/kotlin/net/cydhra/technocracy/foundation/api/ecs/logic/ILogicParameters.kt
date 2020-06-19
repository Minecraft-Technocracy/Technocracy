package net.cydhra.technocracy.foundation.api.ecs.logic

import net.minecraft.entity.player.EntityPlayer


/**
 * Holds additional parameters required for logic client implementations
 */
interface ILogicParameters

object EmptyLogicParameters : ILogicParameters
data class ItemStackLogicParameters(val player: EntityPlayer) : ILogicParameters