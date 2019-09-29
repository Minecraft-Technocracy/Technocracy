package net.cydhra.technocracy.foundation.integration.top.providers

import mcjty.theoneprobe.api.*
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle
import net.cydhra.technocracy.foundation.multiblock.BaseMultiBlock
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatable
import net.cydhra.technocracy.foundation.tileentity.components.*
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

class MachineTOPProvider : IProbeInfoProvider {

    private val energyStyle: ProgressStyle = ProgressStyle().suffix("RF").filledColor(0xffdd0000.toInt()).borderColor(0xff555555.toInt()).alternateFilledColor(0xff430000.toInt()).numberFormat(NumberFormat.COMPACT)
    private val fluidStyle: ProgressStyle = ProgressStyle().suffix("mB").filledColor(0xff0000dd.toInt()).borderColor(0xff555555.toInt()).alternateFilledColor(0xff000043.toInt()).numberFormat(NumberFormat.COMPACT)

    override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
        val te = world.getTileEntity(data.pos) as? TCAggregatable ?: return
        if (te !is ICapabilityProvider) return
        val components: Set<Pair<String, AbstractComponent>>
        components = if (te is TileEntityMultiBlockPart<*>) {
            if (te.multiblockController != null && te.multiblockController!!.isAssembled) (te.multiblockController as BaseMultiBlock).getComponents().toSet() else setOf()
        } else {
            te.getComponents().toSet()
        }
        components.forEach { (_, component) ->
            fillInfo(component, te, probeInfo)
        }
    }

    fun fillInfo(component: AbstractComponent, te: TCAggregatable, probeInfo: IProbeInfo) {
        when (component) {
            is EnergyStorageComponent ->
                if (!(te as ICapabilityProvider).hasCapability(CapabilityEnergy.ENERGY, null))
                    probeInfo.progress(component.energyStorage.currentEnergy, component.energyStorage.capacity, energyStyle)
            is FluidComponent ->
                if (!(te as ICapabilityProvider).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
                    probeInfo.progress(component.fluid.currentFluid?.amount
                            ?: 0, component.fluid.capacity, fluidStyle).text(component.fluid.currentFluid?.localizedName
                            ?: "")
            is InventoryComponent -> {
                for (i in 0 until component.inventory.stacks.size) {
                    if (component.inventory.getStackInSlot(i) != ItemStack.EMPTY)
                        probeInfo.item(component.inventory.getStackInSlot(i)).text(component.inventory.getStackInSlot(i).displayName)
                }
            }
            is OptionalAttachedComponent<*> -> {
                if(component.isAttached)
                    fillInfo(component.innerComponent, te, probeInfo)
            }
        }
    }

    override fun getID(): String = "technocracy.foundation.topintegration.machineprovider"

}