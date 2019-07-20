package net.cydhra.technocracy.foundation.tileentity.multiblock.boiler

import net.cydhra.technocracy.foundation.multiblock.BoilerMultiBlock
import net.cydhra.technocracy.foundation.tileentity.AggregatableDelegate
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatable
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

/**
 * The tile entity for the controller block of a boiler multi-block structure
 */
class TileEntityBoilerHeater : TileEntityMultiBlockPart<BoilerMultiBlock>(BoilerMultiBlock::class, ::BoilerMultiBlock) {

    /**
     *The energy storage of this block. It is not part of the boiler structure, as heating is decoupled from it and
     * could be done by other means than energy.
     */
    val energyStorageComponent: EnergyStorageComponent = EnergyStorageComponent(mutableSetOf(EnumFacing.DOWN))

    init {
        this.registerComponent(energyStorageComponent, "energy")
    }

    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    /**
     * Called by the multiblock structure to use energy.
     *
     * @param energy the amount of energy to be consumed
     *
     * @return true if a sufficient amount of energy has been consumed
     */
    fun tryHeating(energy: Int): Boolean {
        return this.energyStorageComponent.energyStorage.consumeEnergy(energy)
    }

    /**
     * When a player activates the block, just give a debug output of the current energy storage
     */
    override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {
        player.sendMessage(TextComponentString("Energy: ${energyStorageComponent.energyStorage
                .currentEnergy}/${energyStorageComponent.energyStorage.capacity}"))
    }

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        return this.serializeNBT(super.writeToNBT(data))
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        this.deserializeNBT(data)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return this.supportsCapability(capability, facing) || super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return this.castCapability(capability, facing) ?: super.getCapability(capability, facing)
    }
}
