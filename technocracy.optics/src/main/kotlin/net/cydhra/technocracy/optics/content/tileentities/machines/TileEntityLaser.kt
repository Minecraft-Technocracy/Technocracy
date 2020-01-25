package net.cydhra.technocracy.optics.content.tileentities.machines

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.model.tileentities.api.TCMachineTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogicClient
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.LogicClientDelegate
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.cydhra.technocracy.optics.api.tileentities.components.LaserEmitterComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing

class TileEntityLaser : AggregatableTileEntity(), TCMachineTileEntity, ILogicClient by LogicClientDelegate() {

    init {
        this.registerComponent(LaserEmitterComponent(arrayOf(EnumFacing.NORTH), limit = -1), "laser_emitter")
    }

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