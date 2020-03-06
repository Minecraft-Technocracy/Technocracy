package net.cydhra.technocracy.foundation.integration.jei.gui

import mezz.jei.api.gui.IDrawable
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.ProgressBar
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.client.Minecraft

class TabDrawable(val tileEntity: MachineTileEntity) : IDrawable {

    var tab: TCTab? = null

    var deltaTime: Double = 0.0

    init {
        val tabs = tileEntity.getGui(null).tabs // Minecraft.getMinecraft().player is at this point null
        tab = if (tabs.isNotEmpty()) tabs[0] else null
    }

    override fun draw(minecraft: Minecraft, x: Int, y: Int) {
        val tab = this.tab ?: return

        for (c in tab.components) {
            if (c is ProgressBar) {
                c.lastprogress = c.progress
                c.progress = deltaTime
                if (c.progress == 0.0)
                    c.lastprogress = 0.0
            }
        }

        tab.draw(x, y, 0, 0, minecraft.renderPartialTicks)
    }

    override fun getWidth(): Int {
        return if (tab == null) 0 else 176
    }

    override fun getHeight(): Int {
        return if (tab == null) 0 else 166
    }

}