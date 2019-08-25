package net.cydhra.technocracy.foundation.integration.jei

import com.google.common.base.Preconditions
import mezz.jei.api.gui.ITickTimer

// copied from https://github.com/mezz/JustEnoughItems/blob/1.12/src/main/java/mezz/jei/gui/TickTimer.java (is not available for jei addons, but i need it for creating the animations)
class TickTimer(ticksPerCycle: Int, private val maxValue: Int, private val countDown: Boolean) : ITickTimer {
    private val msPerCycle: Int
    private val startTime: Long

    init {
        Preconditions.checkArgument(ticksPerCycle > 0, "Must have at least 1 tick per cycle.")
        Preconditions.checkArgument(maxValue > 0, "max value must be greater than 0")
        this.msPerCycle = ticksPerCycle * 50
        this.startTime = System.currentTimeMillis()
    }

    override fun getValue(): Int {
        val currentTime = System.currentTimeMillis()
        return getValue(startTime, currentTime, maxValue, msPerCycle, countDown)
    }

    override fun getMaxValue(): Int {
        return maxValue
    }

    companion object {

        fun getValue(startTime: Long, currentTime: Long, maxValue: Int, msPerCycle: Int, countDown: Boolean): Int {
            val msPassed = (currentTime - startTime) % msPerCycle
            val value = Math.floorDiv(msPassed * (maxValue + 1), msPerCycle.toLong()).toInt()
            return if (countDown) {
                maxValue - value
            } else {
                value
            }
        }
    }
}