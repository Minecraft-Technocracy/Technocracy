package net.cydhra.technocracy.optics.content.tileentities.machines

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.model.tileentities.api.TCMachineTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogicClient
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.LogicClientDelegate
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.minecraft.entity.player.EntityPlayer

class TileEntityLaser : AggregatableTileEntity(), TCMachineTileEntity, ILogicClient by LogicClientDelegate() {

    override fun update() {
        // tick logic clients
        if (!this.world.isRemote)
            this.tick()
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val gui = TCGui(container = TCContainer())

        // TODO

        return gui
    }
}