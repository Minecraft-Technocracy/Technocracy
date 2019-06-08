package net.cydhra.technocracy.foundation.util

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import java.util.*


interface INumberProvider {
    fun intValue(world: World, rand: Random, data: DataHolder): Int {

        return longValue(world, rand, data).toInt()
    }

    fun longValue(world: World, rand: Random, pos: DataHolder): Long

    fun floatValue(world: World, rand: Random, data: DataHolder): Float {

        return doubleValue(world, rand, data).toFloat()
    }

    fun doubleValue(world: World, rand: Random, data: DataHolder): Double {

        return longValue(world, rand, data).toDouble()
    }

     class DataHolder(start: BlockPos) {

        private val data = Object2ObjectOpenHashMap<String, Any>(16)

        init {

            setValue("start", start).setPosition(start)
            setValue("chunk", Vec3i(start.getX() shr 4, 0, start.getZ() shr 4))
        }

        fun getPos(key: String): Vec3i {

            return data[key] as Vec3i
        }

        fun setValue(key: String, value: Any): DataHolder {

            data[key] = value
            return this
        }

        fun setPosition(pos: Vec3i): DataHolder {

            setValue("position", pos)
            return this
        }
    }
}