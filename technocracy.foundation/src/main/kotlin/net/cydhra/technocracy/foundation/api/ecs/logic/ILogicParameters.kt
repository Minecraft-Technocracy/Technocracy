package net.cydhra.technocracy.foundation.api.ecs.logic

import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.cydhra.technocracy.foundation.util.getSide
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.fml.relauncher.Side


/**
 * Holds additional parameters required for logic client implementations
 */
interface ILogicParameters

object EmptyLogicParameters : ILogicParameters
data class ItemStackLogicParameters(val player: EntityPlayer, val data: ItemStackTickData, val type: ItemStackTickType = data.type) : ILogicParameters {
    constructor(player: EntityPlayer, type: ItemStackTickType) : this(player, EmptyStackData(type))

    lateinit var wrapper: ItemCapabilityWrapper
    val side = player.getSide()
}

enum class ItemStackTickType {
    ARMOR_TICK, ENTITY_DAMAGE, ENTITY_ATTACK, EQUIP_STATE_CHANGE
}

abstract class ItemStackTickData(val type: ItemStackTickType)

class EmptyStackData(type: ItemStackTickType) : ItemStackTickData(type)

class EntityDamageData(val event: LivingDamageEvent, val armor: Boolean) : ItemStackTickData(ItemStackTickType.ENTITY_DAMAGE)
class EntityAttackData(val event: LivingAttackEvent, val armor: Boolean) : ItemStackTickData(ItemStackTickType.ENTITY_ATTACK)
class EquipmentData(val from: ItemStack, val to: ItemStack, val armor: Boolean, val state: EquipState) : ItemStackTickData(ItemStackTickType.EQUIP_STATE_CHANGE) {
    enum class EquipState { EQUIP, UNEQUIP }
}
