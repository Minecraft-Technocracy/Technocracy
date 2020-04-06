package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.container.TCContainer


interface TCGui {
    val origWidth: Int
    val origHeight: Int

    var guiWidth: Int
    var guiHeight: Int
    var guiX: Int
    var guiY: Int

    val container: TCContainer
    //val tabs: ArrayList<TCTab>

    fun setActiveTab(index: Int)
    fun getActiveTab() : TCTab
    fun getTabs(): List<TCTab>
    fun registerTab(tab: TCTab)
    fun getTab(index: Int): TCTab
}