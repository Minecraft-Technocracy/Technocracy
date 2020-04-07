package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.container.TCContainer


class SimpleGui(override var guiWidth: Int = 176,override var guiHeight: Int = 166, override val container: TCContainer) : TCGui {
    override val origWidth = guiWidth
    override val origHeight = guiHeight
    override var guiX = 0
    override var guiY = 0

    override fun registerTab(tab: TCTab) {
        tab.init()
        container.registerTab(tab)
    }

    override fun getTab(index: Int): TCTab {
        return container.tabs[index]
    }

    override fun getActiveTab(): TCTab {
        return container.tabs[container.activeTab]
    }

    override fun getTabs(): List<TCTab> {
        return container.tabs
    }

    override fun setActiveTab(index: Int) {
        container.activeTab = index
    }
}