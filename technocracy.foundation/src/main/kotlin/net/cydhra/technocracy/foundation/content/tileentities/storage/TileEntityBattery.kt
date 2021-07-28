package net.cydhra.technocracy.foundation.content.tileentities.storage

import net.cydhra.technocracy.foundation.api.ecs.IAggregatableGuiProvider
import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.machine.MachineUpgradesTab
import net.cydhra.technocracy.foundation.client.gui.machine.SideConfigTab
import net.cydhra.technocracy.foundation.content.blocks.batteryBlock
import net.cydhra.technocracy.foundation.content.tileentities.AggregatableTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityEnergyStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityMachineUpgradesComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * A tile entity linked to a pulverizer block that can store up to two stacks of items and processes the first stack
 * into its output (second) stack, if the processing output and second stack can be merged.
 */
class TileEntityBattery : AggregatableTileEntity(), TCTileEntityGuiProvider, IAggregatableGuiProvider {

    val energyStorageComponent = TileEntityEnergyStorageComponent(mutableSetOf(*EnumFacing.values()))
    val upgradesComponent = TileEntityMachineUpgradesComponent(9)

    init {
        energyStorageComponent.energyStorage.capacity = 32_000
        energyStorageComponent.energyStorage.receivingLimit = 32_000
        energyStorageComponent.energyStorage.extractionLimit = 32_000
        registerComponent(energyStorageComponent, "energy")
        registerComponent(upgradesComponent, "upgrades")
    }

    @SideOnly(Side.CLIENT)
    override fun getGui(player: EntityPlayer?, gui: TCGui?): TCGui {
        val parent = gui ?: SimpleGui(container = (TCContainer(this)))
        val mainTab = object : TCTab(name = "Stored Energy", parent = parent, icon = TCIcon(batteryBlock)) {
            override fun init() {

                components.addElement(
                    DefaultEnergyMeter(
                        this.getSizeX() / 2 - 5,
                        20,
                        this@TileEntityBattery.energyStorageComponent,
                        parent
                    )
                )

                if (player != null)
                    addPlayerInventorySlots(player, 8, parent.guiHeight - 58 - 16 - 5 - 12)
            }
        }
        parent.registerTab(mainTab)
        parent.registerTab(
            MachineUpgradesTab(
                parent = parent,
                upgrades = this@TileEntityBattery.upgradesComponent,
                player = player
            )
        )
        parent.registerTab(SideConfigTab(parent = parent, machine = this, mainTab = mainTab))
        return parent
    }

    override fun canInteractWith(player: EntityPlayer?): Boolean {
        if (player == null)
            return true
        return player.isEntityAlive && !tile.isInvalid && player.getDistanceSq(tile.pos) <= 16
    }
}