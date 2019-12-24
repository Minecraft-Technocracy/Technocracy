package net.cydhra.technocracy.foundation.content.tileentities.multiblock.capacitor

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.label.DefaultLabel
import net.cydhra.technocracy.foundation.client.gui.multiblock.BaseMultiblockTab
import net.cydhra.technocracy.foundation.content.multiblock.CapacitorMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

class TileEntityCapacitorController : TileEntityMultiBlockPart<CapacitorMultiBlock>(CapacitorMultiBlock::class,
        ::CapacitorMultiBlock), ITileEntityMultiblockController {

    val energyStorageComponent = EnergyStorageTileEntityComponent(EnumFacing.values().toMutableSet())

    init {
        energyStorageComponent.energyStorage.capacity = 100000
        this.registerComponent(energyStorageComponent, "storage")
    }

    override fun initGui(gui: TCGui) {
        gui.tabs.clear() // remove main menu
        gui.registerTab(object : BaseMultiblockTab(this, gui, ResourceLocation("technocracy.foundation", "textures/item/silicon.png")) {
            override fun init() {
                (this@TileEntityCapacitorController.multiblockController as BaseMultiBlock).getComponents().forEach { (_, guiComponent) ->
                    when (guiComponent) {
                        is EnergyStorageTileEntityComponent -> {
                            components.add(DefaultEnergyMeter(10, 20, guiComponent, gui))
                        }
                    }
                }
                components.add(DefaultLabel(25, 25, "Transfer Limit:"))

                addPlayerInventorySlots(Minecraft.getMinecraft().player, 8, 84)
            }

            override fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
                super.draw(mouseX, mouseY, partialTicks)
                Minecraft.getMinecraft().fontRenderer.drawString("${this@TileEntityCapacitorController.multiblockController?.controllerTileEntity?.energyStorageComponent?.energyStorage?.extractionLimit} RF"
                        ?: "0 RF", 25f, 35f, -1, true)
            }

        })
    }

}