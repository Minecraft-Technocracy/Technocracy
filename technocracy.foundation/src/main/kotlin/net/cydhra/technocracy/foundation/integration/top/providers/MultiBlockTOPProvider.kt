package net.cydhra.technocracy.foundation.integration.top.providers

import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.ProbeMode
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle
import net.cydhra.technocracy.foundation.blocks.MultiBlockRotatableActiveBlock
import net.cydhra.technocracy.foundation.multiblock.BaseMultiBlock
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.TileEntityMultiBlockPart
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class MultiBlockTOPProvider : IProbeInfoProvider {

    val energyStyle: ProgressStyle = ProgressStyle().suffix("RF").filledColor(0xffff0000.toInt()).borderColor(0xff555555.toInt()).alternateFilledColor(0xff000000.toInt())

    override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
        if (blockState.block !is MultiBlockRotatableActiveBlock<*>) return
        val te = world.getTileEntity(data.pos) as? TileEntityMultiBlockPart<*> ?: return
        val info = probeInfo.vertical()
        (te.multiblockController as BaseMultiBlock).getComponents().forEach {
            if (it.second is EnergyStorageComponent) {
                val component = it.second as EnergyStorageComponent
                info.horizontal().progress(component.energyStorage.currentEnergy, component.energyStorage.capacity, energyStyle)
            }
        }
    }

    override fun getID(): String = "technocracy.foundation.topintegration.multiblockprovider"
}