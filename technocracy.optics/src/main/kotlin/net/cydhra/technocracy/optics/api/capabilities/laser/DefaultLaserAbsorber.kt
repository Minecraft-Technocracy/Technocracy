package net.cydhra.technocracy.optics.api.capabilities.laser

import net.minecraft.util.EnumFacing

/**
 * The default implementation forge requires to exist. This does only throw exceptions when used, so another
 * implementation must be provided whenever the capability is used.
 */
class DefaultLaserAbsorber : ILaserAbsorber {
    override fun acceptsLaser(facing: EnumFacing): Boolean {
        TODO("not implemented")
    }

    override fun beginTransmission(facing: EnumFacing, energyPerTick: Long, emitter: ILaserEmitter) {
        TODO("not implemented")
    }

    override fun endTransmission(facing: EnumFacing, emitter: ILaserEmitter) {
        TODO("not implemented")
    }

}