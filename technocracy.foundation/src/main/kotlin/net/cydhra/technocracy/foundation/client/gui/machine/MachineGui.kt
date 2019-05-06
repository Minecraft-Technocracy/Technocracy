package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.minecraft.entity.player.EntityPlayer

open class MachineGui(player: EntityPlayer, private val machine: MachineTileEntity?, container: TCContainer) :
        TCGui(player, container = container) {

}

