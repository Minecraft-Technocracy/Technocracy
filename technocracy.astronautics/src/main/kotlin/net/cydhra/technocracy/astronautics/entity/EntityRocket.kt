package net.cydhra.technocracy.astronautics.entity


import net.cydhra.technocracy.foundation.util.structures.Template
import net.minecraft.entity.Entity
import net.minecraft.entity.MoverType
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
import kotlin.math.min
import kotlin.math.max
import net.minecraftforge.event.world.GetCollisionBoxesEvent as GetCollisionBoxesEvent1

open class EntityRocket(world: World) : Entity(world) {

    override fun setItemStackToSlot(slotIn: EntityEquipmentSlot, stack: ItemStack) {
    }

    override fun getArmorInventoryList(): MutableIterable<ItemStack> {
        return Collections.emptyList()
    }

    constructor(world: World, template: Template) : this(world) {
        this.template = template
    }

    var template: Template = Template()

    init {
        template.loadFromAssets("launchpad")
        MinecraftForge.EVENT_BUS.register(this)
    }

    lateinit var entityBox: AxisAlignedBB

    var lastBB: List<AxisAlignedBB> = mutableListOf()

    override fun onUpdate() {
        setNoGravity(true)
        noClip = false

        this.motionX = 0.05
        this.motionY = 0.0

        super.onUpdate()

        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        val d0 = this.motionX
        val d1 = this.motionY
        val d2 = this.motionZ

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)

        if (!this.world.isRemote) {
            val d3 = this.motionX - d0
            val d4 = this.motionY - d1
            val d5 = this.motionZ - d2
            val d6 = d3 * d3 + d4 * d4 + d5 * d5

            if (d6 > 0.01) {
                this.isAirBorne = true
            }
        }

        val list = this.world.getEntitiesInAABBexcluding(this, this.entityBoundingBox, EntitySelectors.getTeamCollisionPredicate(this))
        if (list.isNotEmpty()) {
            for (entity in list) {
                this.collideWithEntity(entity)
            }
        }
    }

    @SubscribeEvent
    fun addBB(e: GetCollisionBoxesEvent1) {
        if (e.entity != null) {
            lastBB = getBlockBounds(e.aabb.grow(0.025), e.entity)
            e.collisionBoxesList.addAll(lastBB)
        }
    }

    override fun move(type: MoverType, x: Double, y: Double, z: Double) {
        if (y != 0.0) {
            posY += y
        }

        if (x != 0.0) {
            posX += x
        }

        if (z != 0.0) {
            posZ += z
        }
    }

    fun getBlockBounds(bb: AxisAlignedBB, entity: Entity): List<AxisAlignedBB> {
        val list = mutableListOf<AxisAlignedBB>()

        val offX = posX - position.x - 0.5
        val offY = posY - position.y
        val offZ = posZ - position.z - 0.5

        for (info in template.blocks) {
            val innerList = mutableListOf<AxisAlignedBB>()
            val state = info.block.getStateFromMeta(info.meta)
            state.addCollisionBoxToList(this.world, info.pos.add(position.x, position.y, position.z), bb.offset(-offX, -offY, -offZ), innerList, entity, false)
            list.addAll(innerList.map { it.offset(offX, offY, offZ) }.toList())
        }

        return list
    }

    override fun getEntityBoundingBox(): AxisAlignedBB {
        if (!::entityBox.isInitialized) {
            var minX = 0
            var minY = 0
            var minZ = 0
            var maxX = 0
            var maxY = 0
            var maxZ = 0

            for (info in template.blocks) {
                minX = min(info.pos.x, minX)
                minY = min(info.pos.y, minY)
                minZ = min(info.pos.z, minZ)
                maxX = max(info.pos.x, maxX)
                maxY = max(info.pos.y, maxY)
                maxZ = max(info.pos.z, maxZ)
            }

            entityBox = AxisAlignedBB(minX.toDouble() - 1, minY.toDouble(), minZ.toDouble() - 1, maxX.toDouble() + 1, maxY.toDouble() + 1, maxZ.toDouble() + 1)
        }

        return entityBox.offset(posX, posY, posZ)
    }

    override fun writeEntityToNBT(compound: NBTTagCompound) {
        compound.setTag("blocks", template.serializeNBT())
    }

    override fun readEntityFromNBT(compound: NBTTagCompound) {
        template.deserializeNBT(compound.getCompoundTag("blocks"))
    }

    protected fun collideWithEntity(entityIn: Entity) {
        val boosted = mutableListOf<Entity>()
        for (bb in getBlockBounds(entityIn.entityBoundingBox, entityIn)) {
            val list = this.world.getEntitiesWithinAABBExcludingEntity(this, bb.grow(1.0))
            for (entity in list.filter { !boosted.contains(it) }) {
                if (entity !is EntityRocket) {
                    boosted.add(entity)
                    if ((lastTickPosY - posY) != 0.0) {
                        entity.motionY = 0.0
                        entity.move(MoverType.PLAYER, motionX, motionY, motionZ)
                        entity.onGround = true
                    }
                }

            }
        }
    }

    override fun entityInit() {
    }
}