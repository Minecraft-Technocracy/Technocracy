package net.cydhra.technocracy.optics.api.tileentities.components

import net.cydhra.technocracy.foundation.content.tileentities.components.AbstractCapabilityTileEntityComponent
import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.optics.api.capabilities.laser.ILaserAbsorber
import net.cydhra.technocracy.optics.api.capabilities.laser.ILaserEmitter
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

class LaserAbsorberComponent(private val canAbsorbAt: Array<EnumFacing>) : AbstractCapabilityTileEntityComponent(), ILaserAbsorber {

    companion object {
        @JvmStatic
        @CapabilityInject(ILaserAbsorber::class)
        lateinit var CAPABILITY_LASER_ABSORBER: Capability<ILaserAbsorber>
    }

    override val type: ComponentType = ComponentType.OTHER

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (this.hasCapability(capability, facing)) {
            CAPABILITY_LASER_ABSORBER.cast<T>(this)
        } else {
            null
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CAPABILITY_LASER_ABSORBER && (facing == null || this.acceptsLaser(facing))
    }

    override fun serializeNBT(): NBTTagCompound {
        return CAPABILITY_LASER_ABSORBER.storage.writeNBT(CAPABILITY_LASER_ABSORBER, this, null) as NBTTagCompound
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        CAPABILITY_LASER_ABSORBER.storage.readNBT(CAPABILITY_LASER_ABSORBER, this, null, nbt)
    }

    override fun acceptsLaser(facing: EnumFacing): Boolean {
        return this.canAbsorbAt.contains(facing)
    }

    override fun beginTransmission(facing: EnumFacing, energyPerTick: Long, emitter: ILaserEmitter) {

    }

    override fun endTransmission(facing: EnumFacing, emitter: ILaserEmitter) {

    }

}