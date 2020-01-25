package net.cydhra.technocracy.optics.api.tileentities.components

import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractCapabilityTileEntityComponent
import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.foundation.util.compound
import net.cydhra.technocracy.optics.api.capabilities.laser.ILaserEmitter
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

class LaserEmitterComponent(private val canEmitAt: Array<EnumFacing>, var limit: Int)
    : AbstractCapabilityTileEntityComponent(), ILaserEmitter {

    companion object {
        @JvmStatic
        @CapabilityInject(ILaserEmitter::class)
        lateinit var CAPABILITY_LASER_EMITTER: Capability<ILaserEmitter>

        private const val NBT_KEY_EMITTER_CAP = "emitter"
        private const val NBT_KEY_LIMIT = "limit"
    }

    override val type: ComponentType = ComponentType.OTHER

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (this.hasCapability(capability, facing)) {
            CAPABILITY_LASER_EMITTER.cast<T>(this)
        } else {
            null
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CAPABILITY_LASER_EMITTER && (facing == null || this.canEmitLaser(facing))
    }

    override fun serializeNBT(): NBTTagCompound {
        return compound {
            NBT_KEY_EMITTER_CAP to CAPABILITY_LASER_EMITTER.storage.writeNBT(CAPABILITY_LASER_EMITTER, this@LaserEmitterComponent, null)
            NBT_KEY_LIMIT to this@LaserEmitterComponent.limit
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        CAPABILITY_LASER_EMITTER.storage.readNBT(CAPABILITY_LASER_EMITTER, this, null, nbt.getCompoundTag(NBT_KEY_EMITTER_CAP))
        this.limit = nbt.getInteger(NBT_KEY_LIMIT)
    }

    override fun stopTransmission(facing: EnumFacing) {
        // TODO
    }

    override fun canEmitLaser(facing: EnumFacing): Boolean {
        return this.canEmitAt.contains(facing)
    }


}