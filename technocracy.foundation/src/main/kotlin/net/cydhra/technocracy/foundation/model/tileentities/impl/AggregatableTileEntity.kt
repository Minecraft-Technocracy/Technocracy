package net.cydhra.technocracy.foundation.model.tileentities.impl

import net.cydhra.technocracy.foundation.model.tileentities.api.AbstractTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatableTileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class AggregatableTileEntity : AbstractTileEntity(), TCAggregatableTileEntity, TCAggregatable by AggregatableDelegate() {

    init {
        this.tile = this
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        this.serializeNBT(compound)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.deserializeNBT(compound)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return this.supportsCapability(capability, facing) || super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return this.castCapability(capability, facing) ?: super.getCapability(capability, facing)
    }
}