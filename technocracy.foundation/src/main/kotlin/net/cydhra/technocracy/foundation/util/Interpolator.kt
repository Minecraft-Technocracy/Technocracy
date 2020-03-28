package net.cydhra.technocracy.foundation.util

import javax.vecmath.Vector3d
import kotlin.math.abs

object Interpolator {

    fun easeCircIn(t: Float, b: Double, c: Double, d: Float): Double {
        var t = t
        return c * d.let { t /= it; t } * t * t + b
    }

    fun circInInterpolate(from: Double, to: Double, timeFrom: Float, timeTo: Float, currentTime: Float): Double {
        return linearInterpolate(from, to, timeFrom, timeTo, currentTime)//from + (to - from) * easeCircIn(currentTime - timeFrom, 0.0, 1.0, timeTo - timeFrom)
    }

    fun linearInterpolate(from: Double, to: Double, percentage: Float): Double {
        return from + (to - from) * percentage
    }

    fun linearInterpolate(from: Float, to: Float, timeFrom: Float, timeTo: Float, currentTime: Float): Float {
        val delta = currentTime - timeFrom
        val distance = timeTo - timeFrom
        val percentage = delta / distance

        return from + (to - from) * percentage
    }

    fun linearInterpolate(from: Double, to: Double, timeFrom: Float, timeTo: Float, currentTime: Float): Double {
        val delta = currentTime - timeFrom
        val distance = timeTo - timeFrom
        val percentage = delta / distance

        return from + (to - from) * percentage
    }

    class InterpolationCycle<T : InterpolateableValue<T>> {
        private val steps = mutableListOf<InterpolationStep<T>>()

        fun addStep(value: T, time: Float): InterpolationCycle<T> {
            steps.add(InterpolationStep(time, value))
            return this
        }

        fun getInterpolated(time: Float): T {
            val current = getCurrent(time)
            if (current.time > time) return current.value
            val next = getNext(time) ?: return current.value

            return interpolate(current, next, time)
        }

        fun getCurrent(time: Float): InterpolationStep<T> {
            return steps.filter { it.time < time }.maxBy { it.time } ?: steps.minBy { it.time } ?: steps.first()
        }

        fun getNext(time: Float): InterpolationStep<T>? {
            val current = getCurrent(time)
            return steps.filter { it != current }.filter { it.time > current.time }.minBy { abs(it.time - current.time) }
        }

        private fun interpolate(from: InterpolationStep<T>, to: InterpolationStep<T>, time: Float): T {
            return from.value.interpolate(to.value, from.time, to.time, time)
        }

        fun clear() {
            steps.clear()
        }
    }

    class InterpolationStep<T : InterpolateableValue<T>>(val time: Float, val value: T)

    abstract class InterpolateableValue<T> {
        abstract fun interpolate(to: T, timeFrom: Float, timeTo: Float, currentTime: Float): T
    }

    class InterpolateFloat(val value: Float) : InterpolateableValue<InterpolateFloat>() {
        override fun interpolate(to: InterpolateFloat, timeFrom: Float, timeTo: Float, currentTime: Float): InterpolateFloat {
            return InterpolateFloat(linearInterpolate(value, to.value, timeFrom, timeTo, currentTime))
        }
    }

    open class PosLook(open val pos: Vector3d, open val look: Vector3d) : InterpolateableValue<PosLook>() {

        override fun interpolate(to: PosLook, timeFrom: Float, timeTo: Float, currentTime: Float): PosLook {

            val pos = this.pos
            val topos = to.pos

            val look = this.look
            val tolook = to.look

            val x = circInInterpolate(pos.x, topos.x, timeFrom, timeTo, currentTime)
            val y = circInInterpolate(pos.y, topos.y, timeFrom, timeTo, currentTime)
            val z = circInInterpolate(pos.z, topos.z, timeFrom, timeTo, currentTime)

            val yaw = circInInterpolate(look.x, tolook.x, timeFrom, timeTo, currentTime)
            val pitch = circInInterpolate(look.y, tolook.y, timeFrom, timeTo, currentTime)
            val roll = circInInterpolate(look.z, tolook.z, timeFrom, timeTo, currentTime)

            return PosLook(Vector3d(x, y, z), Vector3d(yaw, pitch, roll))
        }
    }
}