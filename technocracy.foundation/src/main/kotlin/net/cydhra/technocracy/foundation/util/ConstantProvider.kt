package net.cydhra.technocracy.foundation.util

import net.minecraft.world.World
import java.util.*


class ConstantProvider(value: Number?) : INumberProvider {
    protected var min: Number

    init {
        if (value == null) {
            throw IllegalArgumentException("Null value not allowed")
        }
        this.min = value
    }

    override fun longValue(world: World, rand: Random, data: INumberProvider.DataHolder): Long {

        return min.toLong()
    }

    override fun doubleValue(world: World, rand: Random, data: INumberProvider.DataHolder): Double {

        return min.toDouble()
    }
}