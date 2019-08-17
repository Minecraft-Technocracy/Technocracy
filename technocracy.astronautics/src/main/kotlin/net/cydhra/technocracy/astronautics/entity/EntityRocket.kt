package net.cydhra.technocracy.astronautics.entity


import net.cydhra.technocracy.foundation.util.structures.Template
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.min
import kotlin.math.max
import net.minecraftforge.event.world.GetCollisionBoxesEvent as GetCollisionBoxesEvent1

class EntityRocket(world: World) : Entity(world) {

    constructor(world: World, template: Template): this(world) {
        this.template = template
    }

    var template: Template = Template()

    init {
        template.loadFromAssets("launchpad")
        MinecraftForge.EVENT_BUS.register(this)
    }

    lateinit var entityBox: AxisAlignedBB

    override fun onEntityUpdate() {
        super.onEntityUpdate()
    }

    @SubscribeEvent
    fun addBB(e: GetCollisionBoxesEvent1) {
        if (e.entity != null) {
            val rockets = e.world.getEntitiesWithinAABBExcludingEntity(e.entity, e.aabb.grow(0.25)).filterIsInstance<EntityRocket>()

            for (rocket in rockets) {
                e.collisionBoxesList.addAll(rocket.getBlockBounds(e.aabb, e.entity))
            }
        }
    }

    fun getBlockBounds(bb: AxisAlignedBB, entity: Entity): List<AxisAlignedBB> {
        val list = mutableListOf<AxisAlignedBB>()

        for (info in template.blocks) {
            val state = info.block.getStateFromMeta(info.meta)
            state.addCollisionBoxToList(this.world, info.pos.add(this.position), bb, list, entity, false)
        }

        return list
    }

    override fun getEntityBoundingBox(): AxisAlignedBB {

        if(!::entityBox.isInitialized) {
            var minX = 0
            var minY = 0
            var minZ = 0
            var maxX = 0
            var maxY = 0
            var maxZ = 0
            
            for(info in template.blocks) {
                minX = min(info.pos.x, minX)
                minY = min(info.pos.y, minY)
                minZ = min(info.pos.z, minZ)
                maxX = max(info.pos.x, maxX)
                maxY = max(info.pos.y, maxY)
                maxZ = max(info.pos.z, maxZ)
            }

            entityBox = AxisAlignedBB(minX.toDouble(), minY.toDouble(), minZ.toDouble(), maxX.toDouble(), maxY.toDouble(), maxZ.toDouble())
        }

        return entityBox.offset(this.position)
    }

    override fun writeEntityToNBT(compound: NBTTagCompound) {
        compound.setTag("blocks", template.serializeNBT())
    }

    override fun readEntityFromNBT(compound: NBTTagCompound) {
        template.deserializeNBT(compound.getCompoundTag("blocks"))
    }

    override fun entityInit() {

    }
}