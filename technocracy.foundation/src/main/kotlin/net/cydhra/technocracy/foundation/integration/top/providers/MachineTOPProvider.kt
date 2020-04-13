package net.cydhra.technocracy.foundation.integration.top.providers

import mcjty.theoneprobe.api.*
import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.ecs.tileentities.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityEnergyStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityOptionalAttachedComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

class MachineTOPProvider : IProbeInfoProvider {

    /**
     * Whether the progress styles have been implemented already
     */
    private var init = false

    /**
     * The style for technocracy energy bars
     */
    private lateinit var energyStyle: IProgressStyle

    /**
     * The style for technocracy fluid bars
     */
    private lateinit var fluidStyle: IProgressStyle

    /**
     * Add info to the probe, if the [blockstate] is pointing to a tileentity of technocracy.
     */
    override fun addProbeInfo(
            mode: ProbeMode,
            probeInfo: IProbeInfo,
            player: EntityPlayer,
            world: World,
            blockState: IBlockState,
            data: IProbeHitData
    ) {
        if (!init) {
            energyStyle = probeInfo.defaultProgressStyle()
                    .suffix("RF")
                    .filledColor(0xffdd0000.toInt())
                    .borderColor(0xff555555.toInt())
                    .alternateFilledColor(0xff430000.toInt())
                    .numberFormat(NumberFormat.COMPACT)
            fluidStyle = probeInfo.defaultProgressStyle()
                    .suffix(" mB")
                    .filledColor(0xff0000dd.toInt())
                    .borderColor(0xff555555.toInt())
                    .alternateFilledColor(0xff000043.toInt())
                    .numberFormat(NumberFormat.COMMAS)

            init = true
        }

        val te = world.getTileEntity(data.pos) as? TCAggregatableTileEntity
                ?: return
        if (te !is ICapabilityProvider) return

        val components: Set<Pair<String, IComponent>>

        components = if (te is TileEntityMultiBlockPart<*>) {
            if (te.multiblockController != null && te.multiblockController!!.isAssembled)
                (te.multiblockController as BaseMultiBlock).getComponents().toSet() else setOf()
        } else {
            te.getComponents().toSet()
        }

        components.forEach { (_, component) ->
            fillInfo(component, te, probeInfo)
        }
    }

    private fun fillInfo(component: IComponent, te: TCAggregatableTileEntity, probeInfo: IProbeInfo) {
        when (component) {
            is TileEntityEnergyStorageComponent ->
                if (!(te as ICapabilityProvider).hasCapability(CapabilityEnergy.ENERGY, null))
                    probeInfo.progress(component.energyStorage.currentEnergy,
                            component.energyStorage.capacity,
                            energyStyle)
            is TileEntityFluidComponent ->
                if (!(te as ICapabilityProvider).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))

                    probeInfo.progress(component.fluid.currentFluid?.amount
                            ?: 0, component.fluid.capacity, fluidStyle).text(component.fluid.currentFluid?.localizedName
                            ?: "")
            is TileEntityInventoryComponent -> {
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
            is TileEntityOptionalAttachedComponent<*> -> {
                if (component.isAttached)
                    fillInfo(component.innerComponent, te, probeInfo)
            }
        }
    }

    override fun getID(): String = "technocracy.foundation.topintegration.machineprovider"

}