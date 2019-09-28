package net.cydhra.technocracy.astronautics.entity


import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.handler.codec.EncoderException
import net.cydhra.technocracy.astronautics.blocks.general.rocketDriveBlock
import net.cydhra.technocracy.astronautics.fx.ParticleSmoke
import net.cydhra.technocracy.foundation.fx.TCParticleManager
import net.cydhra.technocracy.foundation.util.structures.Template
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.entity.Entity
import net.minecraft.entity.MoverType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTSizeTracker
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTUtil
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.EntitySelectors
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.IOException
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max
import kotlin.math.min
import net.minecraftforge.event.world.GetCollisionBoxesEvent as GetCollisionBoxesEvent1

open class EntityRocket(world: World) : Entity(world), IEntityAdditionalSpawnData {

    constructor(world: World, template: Template, controllerBlock: BlockPos) : this(world) {
        this.template = template
        this.controllerBlock = controllerBlock
    }

    @SideOnly(Side.CLIENT)
    var vbo: VertexBuffer? = null


    lateinit var controllerBlock: BlockPos
    private val LIFTOFF = EntityDataManager.createKey(EntityRocket::class.java, DataSerializers.BOOLEAN)

    var liftOff: Boolean
        get() = true//dataManager.get(LIFTOFF)
        set(value) {
            if (!liftOff) {
                this.motionY = 0.005
            }
            dataManager.set(LIFTOFF, value)
        }


    var template: Template = Template()
    lateinit var entityBox: AxisAlignedBB
    var lastBB: List<AxisAlignedBB> = mutableListOf()

    override fun entityInit() {
    }

    init {
        dataManager.register(LIFTOFF, false)
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun readSpawnData(additionalData: ByteBuf?) {
        if (additionalData == null) return
        template.deserializeNBT(readCompoundTag(additionalData))
    }

    override fun writeSpawnData(buffer: ByteBuf?) {
        if (buffer == null) return
        writeCompoundTag(template.serializeNBT(), buffer)
    }

    @SideOnly(Side.CLIENT)
    override fun onRemovedFromWorld() {
        if (vbo != null)
            vbo!!.deleteGlBuffers()
        super.onRemovedFromWorld()
    }

    @Throws(IOException::class)
    fun readCompoundTag(buf: ByteBuf): NBTTagCompound? {
        val i = buf.readerIndex()
        val b0 = buf.readByte()

        return if (b0.toInt() == 0) {
            null
        } else {
            buf.readerIndex(i)

            try {
                CompressedStreamTools.read(ByteBufInputStream(buf), NBTSizeTracker(2097152L))
            } catch (ioexception: IOException) {
                throw EncoderException(ioexception)
            }

        }
    }

    fun writeCompoundTag(nbt: NBTTagCompound?, buf: ByteBuf) {
        if (nbt == null) {
            buf.writeByte(0)
        } else {
            try {
                CompressedStreamTools.write(nbt, ByteBufOutputStream(buf))
            } catch (ioexception: IOException) {
                throw EncoderException(ioexception)
            }
        }
    }

    override fun setItemStackToSlot(slotIn: EntityEquipmentSlot, stack: ItemStack) {
    }

    override fun getArmorInventoryList(): MutableIterable<ItemStack> {
        return Collections.emptyList()
    }

    override fun onUpdate() {
        setNoGravity(true)
        noClip = false

        this.motionX = 0.0

        if (liftOff) {
            if (ticksExisted % 4 == 0)
                this.motionY *= 1.08
        } else {
            this.motionY = 0.0
        }

        if (posY > 300)
            world.removeEntity(this)

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

        println("update")

        var list = this.world.getEntitiesInAABBexcluding(this, this.entityBoundingBox, EntitySelectors.getTeamCollisionPredicate(this))
        if (list.isNotEmpty()) {
            for (entity in list) {
                if (entity is EntityPlayer)
                    this.collideWithEntity(entity)
            }
        }

        if (world.isRemote) {
            for (info in template.blocks) {
                if (info.pos.y == 0) {
                    if (info.block == rocketDriveBlock) {
                        TCParticleManager.addParticle(ParticleSmoke(world, posX + info.pos.x + ThreadLocalRandom.current().nextFloat(), posY - ThreadLocalRandom.current().nextFloat(), posZ + info.pos.z + ThreadLocalRandom.current().nextFloat()))
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun addBB(e: GetCollisionBoxesEvent1) {
        if (e.entity != null && ::entityBox.isInitialized) {
            if (e.aabb.intersects(entityBoundingBox)) {
                lastBB = getBlockBounds(e.aabb.grow(0.025), e.entity)
                e.collisionBoxesList.addAll(lastBB)
            }
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

        if (!template.init)
            return list

        for (info in template.blocks) {
            val innerList = mutableListOf<AxisAlignedBB>()
            val state = info.block.getStateFromMeta(info.meta)
            info.block.addCollisionBoxToList(state, world, info.pos.add(position.x, position.y, position.z), bb.offset(-offX, -offY, -offZ), innerList, entity, true)
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
        compound.setTag("controller", NBTUtil.createPosTag(controllerBlock))
        compound.setTag("liftOff", template.serializeNBT())
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
                        entity.move(MoverType.SELF, motionX, motionY, motionZ)
                        entity.onGround = true
                    }
                }
            }
        }
    }
}