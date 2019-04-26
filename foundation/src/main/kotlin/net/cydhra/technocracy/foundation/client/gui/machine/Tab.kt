package net.cydhra.technocracy.foundation.client.gui.machine

abstract class Tab(protected val width: Int, protected val height: Int) {
    abstract fun draw(mouseX: Int, mouseY: Int, partialTicks: Float)

    abstract fun update()

}
