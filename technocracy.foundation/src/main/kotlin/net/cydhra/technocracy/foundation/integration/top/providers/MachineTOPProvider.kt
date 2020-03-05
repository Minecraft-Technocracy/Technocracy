package net.cydhra.technocracy.foundation.integration.top.providers

import mcjty.theoneprobe.api.*
import mcjty.theoneprobe.apiimpl.styles.ItemStyle
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.OptionalAttachedTileEntityComponent
import net.cydhra.technocracy.foundation.model.components.IComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

class MachineTOPProvider : IProbeInfoProvider {

    private val energyStyle: ProgressStyle = ProgressStyle().suffix("RF").filledColor(0xffdd0000.toInt()).borderColor(0xff555555.toInt()).alternateFilledColor(0xff430000.toInt()).numberFormat(NumberFormat.COMPACT)
    private val fluidStyle: ProgressStyle = ProgressStyle().suffix(" mB").filledColor(0xff0000dd.toInt()).borderColor(0xff555555.toInt()).alternateFilledColor(0xff000043.toInt()).numberFormat(NumberFormat.COMMAS)

    override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
        val te = world.getTileEntity(data.pos) as? TCAggregatable
                ?: return
        if (te !is ICapabilityProvider) return
        val components: Set<Pair<String, IComponent>>
        components = if (te is TileEntityMultiBlockPart<*>) {
            if (te.multiblockController != null && te.multiblockController!!.isAssembled) (te.multiblockController as BaseMultiBlock).getComponents().toSet() else setOf()
        } else {
            te.getComponents().toSet()
        }
        components.forEach { (_, component) ->
            fillInfo(component, te, probeInfo)
        }
    }

    fun fillInfo(component: IComponent, te: TCAggregatable, probeInfo: IProbeInfo) {
        when (component) {
            is EnergyStorageTileEntityComponent ->
                if (!(te as ICapabilityProvider).hasCapability(CapabilityEnergy.ENERGY, null))
                    probeInfo.progress(component.energyStorage.currentEnergy,
                            component.energyStorage.capacity,
                            energyStyle)
            is FluidTileEntityComponent ->
                if (!(te as ICapabilityProvider).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
                    probeInfo.progress(component.fluid.currentFluid?.amount
                            ?: 0, component.fluid.capacity, fluidStyle).text(component.fluid.currentFluid?.localizedName
                            ?: "")
            is InventoryTileEntityComponent -> {
                val horizontalStyleElement = probeInfo.horizontal(
                        probeInfo.defaultLayoutStyle()
                                .alignment(ElementAlignment.ALIGN_CENTER)
                                .spacing(8))

                for (i in 0 until component.inventory.stacks.size) {
                    if (component.inventory.getStackInSlot(i) != ItemStack.EMPTY) {
                        horizontalStyleElement.item(component.inventory.getStackInSlot(i))

                        if (component.inventory.stacks.filter { !it.isEmpty }.size == 1) {
                            horizontalStyleElement.itemLabel(component.inventory.getStackInSlot(i))
                        }
                    }
                }
            }
            is OptionalAttachedTileEntityComponent<*> -> {
                if(component.isAttached)
                    fillInfo(component.innerComponent, te, probeInfo)
            }
        }
    }

    override fun getID(): String = "technocracy.foundation.topintegration.machineprovider"

}