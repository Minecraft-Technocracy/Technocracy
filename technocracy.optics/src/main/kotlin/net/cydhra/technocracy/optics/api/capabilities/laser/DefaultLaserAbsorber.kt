package net.cydhra.technocracy.optics.api.capabilities.laser

import net.minecraft.util.EnumFacing

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