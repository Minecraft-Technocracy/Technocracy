package net.cydhra.technocracy.optics.content.capabilities.laser

import net.minecraft.util.EnumFacing

/**
 * This capability describes that a block is able to absorb a laser and do something with the transferred energy.
 */
interface ILaserAbsorber {

    /**
     * Whether a specific side of the absorber currently accepts laser input.
     *
     * @param facing the block facing where the laser would hit
     */
    fun acceptsLaser(facing: EnumFacing): Boolean

    /**
     * Start the transmission of energy using a laser at the given block face. If the face already accepted a laser,
     * the old `energyPerTick` value gets overridden by the new one.
     *
     * @param facing where the laser enters the block
     * @param energyPerTick how much energy the laser transfers uses per tick.
     * @param emitter the emitter that is sending energy through the laser
     */
    fun beginTransmission(facing: EnumFacing, energyPerTick: Long, emitter: ILaserEmitter)

    /**
     * End the incoming laser at the given face. No more energy is provided.
     *
     * @param facing Which face no longer is exposed to a laser.
     * @param emitter the emitter that was sending energy to the laser.
     */
    fun endTransmission(facing: EnumFacing, emitter: ILaserEmitter)
}