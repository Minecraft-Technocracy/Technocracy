package net.cydhra.technocracy.foundation.content.tileentities.multiblock.saline

import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.heatmeter.DefaultHeatMeter
import net.cydhra.technocracy.foundation.client.gui.components.label.WrappingLabel
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.multiblock.BaseMultiblockTab
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.items.siliconItem
import net.cydhra.technocracy.foundation.content.multiblock.SalineMultiBlock
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityFluidComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityHeatStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.ITileEntityMultiblockController
import net.cydhra.technocracy.foundation.content.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer

class TileEntitySalineController :
        TileEntityMultiBlockPart<SalineMultiBlock>(SalineMultiBlock::class, ::SalineMultiBlock),
        ITileEntityMultiblockController {

    //Fluids
    val fluidInputComponent = TileEntityFluidComponent(capacity = 8000, facing = mutableSetOf(),
            tanktype = DynamicFluidCapability.TankType.INPUT)
    val fluidOutputComponent = TileEntityFluidComponent(capacity = 8000, facing = mutableSetOf(),
            tanktype = DynamicFluidCapability.TankType.OUTPUT)

    //Heat
    val heatComponent = TileEntityHeatStorageComponent(0, 1000)
    val heatingFluidInputComponent = TileEntityFluidComponent(capacity = 8000, facing = mutableSetOf(),
            tanktype = DynamicFluidCapability.TankType.INPUT)
    val heatingFluidOutputComponent = TileEntityFluidComponent(capacity = 8000, facing = mutableSetOf(),
            tanktype = DynamicFluidCapability.TankType.OUTPUT)

    init {
        registerComponent(fluidInputComponent, "input")
        registerComponent(heatingFluidInputComponent, "heat_input")
        registerComponent(heatComponent, "heat")
        registerComponent(fluidOutputComponent, "output")
        registerComponent(heatingFluidOutputComponent, "heat_output")
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val gui = SimpleGui(container = TCContainer(this))
        gui.registerTab(object : BaseMultiblockTab(this, gui, TCIcon(siliconItem)) {
            private val skyLightLabel = WrappingLabel(
                    posX = 38,
                    posY = 43,
                    maxWidth = 100,
                    text = "",
                    scaling = 0.6,
                    shadow = true,
                    gui = gui
            )

            override fun init() {
                val fluidMeterY = 20
                components.add(DefaultFluidMeter(10, fluidMeterY, fluidInputComponent, gui))
                components.add(DefaultFluidMeter(50, fluidMeterY, fluidOutputComponent, gui))
                components.add(DefaultFluidMeter(gui.guiWidth - 10 - 48, fluidMeterY, heatingFluidInputComponent, gui))
                components.add(DefaultHeatMeter(gui.guiWidth - 10 - 30, fluidMeterY, heatComponent, gui))
                components.add(DefaultFluidMeter(gui.guiWidth - 10 - 20, fluidMeterY, heatingFluidOutputComponent, gui))

                addPlayerInventorySlots(player!!, 8, gui.guiHeight - 58 - 16 - 5 - 12)
            }

            override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
                super.draw(x, y, mouseX, mouseY, partialTicks)

                Minecraft.getMinecraft().textureManager.bindTexture(TCClientGuiImpl.guiComponents)
                GlStateManager.color(1F, 1F, 1F, 1F)
                val u = if (multiblockController!!.skyLightLevel > 0) 48f else 61f
                Gui.drawModalRectWithCustomSizedTexture(x + 32, y + 39, u, 59f, 13, 13, 256f, 256f)

                if (multiblockController!!.skyLightLevel > 0) {
                    skyLightLabel.text = "§8${multiblockController!!.skyLightLevel}"
                    val labelWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth("${multiblockController!!.skyLightLevel}") * skyLightLabel.scaling
                    skyLightLabel.draw(x - (labelWidth / 2f).toInt(), y, mouseX, mouseY, partialTicks)
                }
            }
        })

        return gui
    }
}