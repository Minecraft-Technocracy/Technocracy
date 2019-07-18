package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.machine.MachineContainer
import net.cydhra.technocracy.foundation.client.gui.tabs.DemoTab
import net.cydhra.technocracy.foundation.client.gui.tabs.TCTab
import net.cydhra.technocracy.foundation.client.gui.tabs.WipTab
import net.cydhra.technocracy.foundation.tileentity.api.TCMachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.components.MachineUpgradesComponents
import net.cydhra.technocracy.foundation.tileentity.components.RedstoneModeComponent
import net.cydhra.technocracy.foundation.tileentity.logic.ILogicClient
import net.cydhra.technocracy.foundation.tileentity.logic.LogicClientDelegate
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

open class MachineTileEntity : AggregatableTileEntity(), TCMachineTileEntity, ILogicClient by LogicClientDelegate() {
    /**
     * The machine's redstone mode
     */
    protected val redstoneModeComponent = RedstoneModeComponent()

    /**
     * The machines internal energy storage and transfer limit state
     */
    protected val energyStorageComponent = EnergyStorageComponent(mutableSetOf(EnumFacing.DOWN))

    /**
     * The machine upgrades component.
     */
    /* TODO as the possible upgrades are dependant of machine type, either split this compound into single upgrades or
        at least handle it from subclass*/
    protected val machineUpgradesComponent = MachineUpgradesComponents()

    init {
        this.registerComponent(redstoneModeComponent, "redstone_mode")
        this.registerComponent(energyStorageComponent, "energy")
        this.registerComponent(machineUpgradesComponent, "upgrades")
    }

    override fun getGui(player: EntityPlayer): TCGui {
        val gui = TCGui(player, container = MachineContainer(this))
        gui.registerTab(DemoTab(gui, this, player))
        gui.registerTab(object : TCTab("Tab mit Inventar", gui, icon = ResourceLocation("technocracy.foundation",
                "textures/item/silicon.png")) {

            override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
                super.draw(mouseX, mouseY, partialTicks)

                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Dies ist ein Tab mit Inventar", 8F, 8F, -1)
            }

            override fun update() {
            }

            override fun init() {
                this.addPlayerInventorySlots(player, 8, 84)
            }

        })
        gui.registerTab(WipTab(gui))

        return gui
    }

    override fun update() {
        // update ILogic strategies
        this.tick()
    }
}