package net.cydhra.technocracy.optics.content.tileentities.machines

import net.cydhra.technocracy.foundation.api.ecs.IAggregatableGuiProvider
import net.cydhra.technocracy.foundation.api.ecs.logic.EmptyLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicClient
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.LogicClientDelegate
import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityEnergyStorageComponent
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.cydhra.technocracy.optics.api.tileentities.components.LaserEmitterTileEntityComponent
import net.cydhra.technocracy.optics.api.tileentities.logic.LaserLogic
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable

class TileEntityLaser : AggregatableTileEntity(), IAggregatableGuiProvider, ITickable, TCTileEntityGuiProvider, ILogicClient<ILogicParameters> by LogicClientDelegate() {

    private val energyStorage = TileEntityEnergyStorageComponent(facing = mutableSetOf(EnumFacing.DOWN))
    private val laserEmitter = LaserEmitterTileEntityComponent(setOf(EnumFacing.NORTH), limit = -1)

    init {
        this.registerComponent(laserEmitter, "laser_emitter")
    }

    override fun onLoad() {
        this.addLogicStrategy(LaserLogic(this.pos, this.world, this.laserEmitter, this.energyStorage),
                "laser_logic")
    }

    override fun update() {
        // tick logic clients
        if (!this.world.isRemote)
            this.tick(EmptyLogicParameters)
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val gui = SimpleGui(container = TCContainer(this))

        // TODO

        return gui
    }

    override fun canInteractWith(player: EntityPlayer?): Boolean {
        if (player == null) return true
        return player.isEntityAlive && !tile.isInvalid && player.getDistanceSq(tile.pos) <= 16
    }
}