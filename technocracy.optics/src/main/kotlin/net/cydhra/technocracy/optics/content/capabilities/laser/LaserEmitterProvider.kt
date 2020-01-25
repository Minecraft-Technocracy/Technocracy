package net.cydhra.technocracy.optics.content.capabilities.laser

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * A default provider for the [ILaserEmitter] capability.
 */
class LaserEmitterProvider(val instance: ILaserEmitter) : ICapabilityProvider {
    companion object {
        @JvmStatic
        @CapabilityInject(ILaserEmitter::class)
        lateinit var CAPABILITY_LASER_EMITTER: Capability<ILaserEmitter>
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (this.hasCapability(capability, facing)) {
            CAPABILITY_LASER_EMITTER.cast<T>(this.instance)
        } else {
            null
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CAPABILITY_LASER_EMITTER && (facing == null || this.instance.canEmitLaser(facing))
    }
}