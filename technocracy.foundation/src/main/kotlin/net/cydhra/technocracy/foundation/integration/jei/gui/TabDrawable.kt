package net.cydhra.technocracy.foundation.integration.jei.gui

import mezz.jei.api.gui.IDrawable
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.ProgressBar
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.client.Minecraft

class TabDrawable(val tileEntity: MachineTileEntity) : IDrawable {

    var tab: TCTab? = null

    var deltaTime: Float = 0f

    init {
        val tabs = tileEntity.getGui(null).tabs // Minecraft.getMinecraft().player is at this point null
        tab = if (tabs.isNotEmpty()) tabs[0] else null
    }

    override fun draw(minecraft: Minecraft, x: Int, y: Int) {
        val tab = this.tab ?: return

        for (c in tab.components) {
            if (c is ProgressBar)
                c.progress = deltaTime
        }

        tab.draw(x, y, 0, 0, 0f)
    }

    override fun getWidth(): Int {
        return if (tab == null) 0 else 176
    }

    override fun getHeight(): Int {
        return if (tab == null) 0 else 166
    }

}