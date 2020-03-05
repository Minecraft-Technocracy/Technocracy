package net.cydhra.technocracy.optics.api.capabilities.laser

import net.minecraft.util.EnumFacing

/**
 * The default implementation forge requires to exist. This does only throw exceptions when used, so another
 * implementation must be provided whenever the capability is used.
 */
class DefaultLaserEmitter : ILaserEmitter {
    override fun stopTransmission(facing: EnumFacing) {
        TODO("not implemented")
    }

    override fun canEmitLaser(facing: EnumFacing): Boolean {
        TODO("not implemented")
    }
}