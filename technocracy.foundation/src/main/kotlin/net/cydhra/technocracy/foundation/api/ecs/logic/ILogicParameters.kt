package net.cydhra.technocracy.foundation.api.ecs.logic

import net.cydhra.technocracy.foundation.api.items.capability.ItemCapabilityWrapper
import net.cydhra.technocracy.foundation.util.getSide
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.world.BlockEvent


/**
 * Holds additional parameters required for logic client implementations
 */
interface ILogicParameters

object EmptyLogicParameters : ILogicParameters
data class ItemStackLogicParameters(val player: EntityPlayer, val data: ItemStackTickData, private val type: ItemStackTickType<*> = data.type) : ILogicParameters {
    constructor(player: EntityPlayer, type: ItemStackTickType<*>) : this(player, EmptyStackData(type))

    lateinit var wrapper: ItemCapabilityWrapper
    val side = player.getSide()
}

open class ItemStackTickType<T> {

    companion object {
        private var index = 0
        private val values = mutableListOf<ItemStackTickType<*>>()

        //expose as non mutable list
        fun values(): List<ItemStackTickType<*>> = values

        val ARMOR_TICK = PriorityItemStackTickType<EntityArmorTickData>()
        val ENTITY_DAMAGE = ItemStackTickType<EntityDamageData>()
        val ENTITY_ATTACK = ItemStackTickType<EntityAttackData>()
        val EQUIP_STATE_CHANGE = ItemStackTickType<EquipmentData>()
        val BLOCK_BREAK = ItemStackTickType<BlockBreakData>()
        val BLOCK_BREAK_SPEED = ItemStackTickType<BlockBreakSpeedData>()
    }

    class PriorityItemStackTickType<T> : ItemStackTickType<T>() {
        val LOW = Prio(ItemStackTickPriority.LOW)
        val MEDIUM = Prio(ItemStackTickPriority.MEDIUM)
        val HIGH = Prio(ItemStackTickPriority.HIGH)
    }

    inner class Prio internal constructor(val prio: ItemStackTickPriority) {
        fun getParent(): ItemStackTickType<T> {
            return this@ItemStackTickType
        }
    }

    init {
        values.add(this)
    }

    val ordinal = index++

    infix operator fun get(dat: ItemStackLogicParameters): T {
        return dat.data as T
    }

    infix operator fun get(dat: ItemStackTickData): T {
        return dat as T
    }

    override fun equals(other: Any?): Boolean {
        if (other is ItemStackTickType<*>) {
            return ordinal == other.ordinal
        }
        if (other is ItemStackTickData) {
            return ordinal == other.type.ordinal
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

enum class ItemStackTickPriority {
    LOW, MEDIUM, HIGH
}

abstract class ItemStackTickData(val type: ItemStackTickType<*>) {
    override fun equals(other: Any?): Boolean {
        if (other is ItemStackTickType<*>) {
            return type.ordinal == other.ordinal &&
                    //check if it has priority, if so only call if it is medium
                    if (this is PriorityData) {
                        this.priority == ItemStackTickPriority.MEDIUM
                    } else true
        }
        if (other is ItemStackTickData) {
            return type.ordinal == other.type.ordinal &&
                    //check if it has priority, if so only call if it is medium
                    if (this is PriorityData) {
                        this.priority == ItemStackTickPriority.MEDIUM
                    } else true
        }
        if (other is ItemStackTickType<*>.Prio && this is PriorityData) {
            return other.getParent() == this.type && other.prio == this.priority
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}

class EmptyStackData(type: ItemStackTickType<*>) : ItemStackTickData(type)

open class PriorityData(type: ItemStackTickType<*>, val priority: ItemStackTickPriority) : ItemStackTickData(type)
class EntityArmorTickData(priority: ItemStackTickPriority) : PriorityData(ItemStackTickType.ARMOR_TICK, priority)
class EntityDamageData(val event: LivingDamageEvent, val armor: Boolean) : ItemStackTickData(ItemStackTickType.ENTITY_DAMAGE)
class EntityAttackData(val event: LivingAttackEvent, val armor: Boolean) : ItemStackTickData(ItemStackTickType.ENTITY_ATTACK)
class EquipmentData(val from: ItemStack, val to: ItemStack, val armor: Boolean, val state: EquipState) : ItemStackTickData(ItemStackTickType.EQUIP_STATE_CHANGE) {
    enum class EquipState { EQUIP, UNEQUIP }
}

class BlockBreakData(val event: BlockEvent.BreakEvent) : ItemStackTickData(ItemStackTickType.BLOCK_BREAK)
class BlockBreakSpeedData(val event: PlayerEvent.BreakSpeed) : ItemStackTickData(ItemStackTickType.BLOCK_BREAK_SPEED)