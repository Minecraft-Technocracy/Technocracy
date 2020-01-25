package net.cydhra.technocracy.optics.api.capabilities.laser

import net.minecraft.util.EnumFacing

/**
 * Capability of a block to emit laser at a given face.
 */
interface ILaserEmitter {

    /**
     * Called by an [ILaserAbsorber] when it can no longer accept a laser at its side facing this emitter.
     *
     * @param facing the facing of this emitter where the connected [ILaserAbsorber] can no longer accept a laser
     */
    fun stopTransmission(facing: EnumFacing)

    /**
     * Whether this emitter can emit a laser at the given face
     */
    fun canEmitLaser(facing: EnumFacing): Boolean
}