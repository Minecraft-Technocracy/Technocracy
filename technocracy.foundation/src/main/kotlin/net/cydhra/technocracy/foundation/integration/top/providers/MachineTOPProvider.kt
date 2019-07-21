package net.cydhra.technocracy.foundation.integration.top.providers

import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.ProbeMode
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle
import net.cydhra.technocracy.foundation.blocks.MachineBlock
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class MachineTOPProvider : IProbeInfoProvider {

    val energyStyle = ProgressStyle().suffix("RF").filledColor(0xffff0000.toInt()).borderColor(0xff555555.toInt()).alternateFilledColor(0xff000000.toInt())

    override fun addProbeInfo(mode: ProbeMode?, probeInfo: IProbeInfo?, player: EntityPlayer?, world: World?, blockState: IBlockState?, data: IProbeHitData?) {
        if (blockState!!.block !is MachineBlock) return
        val te = world!!.getTileEntity(data!!.pos) as? MachineTileEntity ?: return
        val info = probeInfo!!.vertical()
        te.getComponents().forEach {
            if (it.second is EnergyStorageComponent) {
                val component = it.second as EnergyStorageComponent
                info.horizontal().progress(component.energyStorage.currentEnergy, component.energyStorage.capacity, energyStyle)
            }
        }
    }

    override fun getID(): String = "technocracy.foundation.topintegration.machineprovider"

}