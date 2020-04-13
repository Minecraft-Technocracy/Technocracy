package net.cydhra.technocracy.foundation.api.ecs

import net.minecraft.entity.player.EntityPlayer


interface IAggregatableGuiProvider : IAggregatable {
    fun canInteractWith(player: EntityPlayer?): Boolean
}