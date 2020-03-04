package net.cydhra.technocracy.foundation.content.tileentities.multiblock.tank

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.multiblock.BaseMultiblockTab
import net.cydhra.technocracy.foundation.client.gui.multiblock.MultiblockContainer
import net.cydhra.technocracy.foundation.client.gui.multiblock.MultiblockSettingsTab
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.multiblock.TankMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.*
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler


open class TileEntityTankMultiBlockPart : TileEntityMultiBlockPart<TankMultiBlock>(TankMultiBlock::class,
        ::TankMultiBlock), ITileEntityMultiblockController {


    val fluidComp = OptionalAttachedTileEntityComponent(FluidTileEntityComponent(DynamicFluidCapability(), EnumFacing.values().toMutableSet()))


    override fun getGui(player: EntityPlayer?): TCGui {

        val gui = TCGui(guiWidth = 176,  guiHeight = 200, container = TCContainer())

        //176, val guiHeight: Int = 166

        gui.registerTab(object : TCTab("Tank", gui) {
            override fun init() {

                val fluid = DefaultFluidMeter(8, 20, this@TileEntityTankMultiBlockPart.multiblockController!!.controllerTileEntity!!.fluidComp.innerComponent, gui)
                fluid.height = gui.guiHeight - 58 - 16 - 5 - 12 - 25
                fluid.width = 8 * 18 - 9

                components.add(fluid)

                if (player != null)
                    addPlayerInventorySlots(player, 8, gui.guiHeight - 58 - 16 - 5 - 12)
            }
        })
        initGui(gui)
        return gui
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return false
    }

    init {
        fluidComp.innerComponent.fluid.fluidChangeThreshold = 1f

        fluidComp.innerComponent.syncToClient = true
        this.registerComponent(fluidComp, "fluidComponent")
    }
}