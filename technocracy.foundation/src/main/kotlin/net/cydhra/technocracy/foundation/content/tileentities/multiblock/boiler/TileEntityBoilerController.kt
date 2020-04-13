package net.cydhra.technocracy.foundation.content.tileentities.multiblock.boiler

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.fluids.steamFluid
import net.cydhra.technocracy.foundation.content.multiblock.BoilerMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityProgressComponent
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

/**
 * The tile entity for the controller block of a boiler multi-block structure
 */
class TileEntityBoilerController
    : TileEntityMultiBlockPart<BoilerMultiBlock>(BoilerMultiBlock::class, ::BoilerMultiBlock), ITileEntityMultiblockController {

    /**
     * The fluid storage for internal usage
     */
    private val internalFluidHandler = DynamicFluidCapability(0, mutableListOf(FluidRegistry.WATER.name),
            DynamicFluidCapability.TankType.INPUT)

    /**
     * The steam storage for internal usage
     */
    private val internalSteamHandler = DynamicFluidCapability(0, mutableListOf(steamFluid.name),
            DynamicFluidCapability.TankType.OUTPUT)

    private val waterComponent = TileEntityFluidComponent(internalFluidHandler, mutableSetOf())
    private val steamComponent = TileEntityFluidComponent(internalSteamHandler, mutableSetOf())
    private val progressComponent = TileEntityProgressComponent()

    /**
     * The fluid storage of this boiler structure. If the structure isn't fully assembled, it is null
     */
    val fluidHandler: DynamicFluidCapability?
        get() {
            if (this.multiblockController?.isAssembled == false)
                return null
            return internalFluidHandler
        }

    /**
     * The output fluid storage for generated steam. If this structure isn't fully assembled, it is null
     */
    val steamHandler: DynamicFluidCapability?
        get() {
            if (this.multiblockController?.isAssembled == false)
                return null
            return internalSteamHandler
        }

    init {
        this.registerComponent(waterComponent, "water")
        this.registerComponent(steamComponent, "steam")
        this.registerComponent(progressComponent, "progress")
    }

    fun doWork() {
        if (this.multiblockController?.isAssembled == true) {
            for (heater in this.multiblockController!!.heaterElements) {
                if (internalSteamHandler.currentFluid?.amount ?: 0 + multiblockController!!.steamPerHeaterPerTick
                        >= internalSteamHandler.capacity)
                    return

                if (internalFluidHandler.currentFluid?.amount ?: 0 < multiblockController!!.steamPerHeaterPerTick)
                    return

                if (heater.tryHeating(multiblockController!!.energyPerHeaterPerTick)) {
                    internalFluidHandler.drain(multiblockController!!.steamPerHeaterPerTick, doDrain = true, forced = true)
                    internalSteamHandler
                            .fill(FluidStack(steamFluid, multiblockController!!.steamPerHeaterPerTick), doFill = true, forced = true)
                }
            }
        }
    }

    /**
     * Update the overall machine capacity to a new value. Current fluid is kept, overflowing parts are drained
     */
    fun updateCapacity(capacity: Int) {
        arrayOf(internalFluidHandler, internalSteamHandler).forEach {
            it.capacity = capacity

            if (it.currentFluid?.amount ?: -1 > capacity) {
                it.drain(it.currentFluid!!.amount - capacity, doDrain = true, forced = true)
            }
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled) {
            this.supportsCapability(capability, facing) || super.hasCapability(capability, facing)
        } else false
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return this.castCapability(capability, facing) ?: super.getCapability(capability, facing)
    }

    override fun onMachineActivated() {

    }

    override fun onMachineDeactivated() {

    }

    /*override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {
        player.sendMessage(TextComponentString("Water: ${internalFluidHandler.currentFluid?.amount
                ?: 0}/${internalFluidHandler.capacity}"))
        player.sendMessage(TextComponentString("Steam: ${internalSteamHandler.currentFluid?.amount
                ?: 0}/${internalSteamHandler.capacity}"))
        player.sendMessage(TextComponentString("Steam per heater: ${multiblockController!!.steamPerHeaterPerTick}/t"))
        player.sendMessage(TextComponentString("Energy per heater: ${multiblockController!!.energyPerHeaterPerTick}/t"))
    }*/


}