package net.cydhra.technocracy.optics.content.capabilities.laser

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * A default provider for the [ILaserAbsorber] capability.
 */
class LaserAbsorberProvider(val instance: ILaserAbsorber) : ICapabilityProvider {
    companion object {
        @JvmStatic
        @CapabilityInject(ILaserAbsorber::class)
        lateinit var CAPABILITY_LASER_ABSORBER: Capability<ILaserAbsorber>
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (this.hasCapability(capability, facing)) {
            CAPABILITY_LASER_ABSORBER.cast<T>(this.instance)
        } else {
            null
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CAPABILITY_LASER_ABSORBER && (facing == null || this.instance.acceptsLaser(facing))
    }
}