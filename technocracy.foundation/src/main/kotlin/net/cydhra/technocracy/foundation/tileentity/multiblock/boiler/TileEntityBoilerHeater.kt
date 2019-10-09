package net.cydhra.technocracy.foundation.tileentity.multiblock.boiler

import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyCapability
import net.cydhra.technocracy.foundation.multiblock.BoilerMultiBlock
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.entity.player.EntityPlayer
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

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled && facing != null) {
            this.supportsCapability(capability, facing) || super.hasCapability(capability, facing)
        } else false
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (this.hasCapability(capability, facing)) super.getCapability(capability, facing)
                ?: DynamicEnergyCapability(0, 1, extractionLimit = 0) as T
        else null
    }
}
