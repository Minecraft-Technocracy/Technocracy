package net.cydhra.technocracy.foundation.config

import net.minecraftforge.common.config.Configuration
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
abstract class Configurable<T : Any>(protected val config: Configuration,
        protected val category: String = Configuration.CATEGORY_GENERAL, protected val name: String,
        protected val defaultValue: T, protected val comment: String) {

    protected companion object {
        var cache = mutableMapOf<Pair<String, String>, Any>()
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        try {
            val key = category to name
            if (cache.containsKey(key)) {
                return cache[key]!! as T
            }

            val option = obtainValue()
            cache[key] = option
            return option
        } finally {
            if (config.hasChanged()) {
                cache.clear()
                config.save()
            }
        }
    }

    abstract fun obtainValue(): T
}

class IntegerConfigurable(config: Configuration, category: String = Configuration.CATEGORY_GENERAL, name: String,
        defaultValue: Int, comment: String, private val minValue: Int, private val maxValue: Int) :
        Configurable<Int>(config, category, name, defaultValue, comment) {

    override fun obtainValue(): Int {
        return config.get(category, name, defaultValue, comment, minValue, maxValue).int
    }
}

class BooleanConfigurable(config: Configuration, category: String = Configuration.CATEGORY_GENERAL, name: String,
        defaultValue: Boolean, comment: String) : Configurable<Boolean>(config, category, name, defaultValue, comment) {
    override fun obtainValue(): Boolean {
        return config.get(category, name, defaultValue, comment).boolean
    }
}


class StringConfigurable(config: Configuration, category: String = Configuration.CATEGORY_GENERAL, name: String,
        defaultValue: String, comment: String) : Configurable<String>(config, category, name, defaultValue, comment) {
    override fun obtainValue(): String {
        return config.get(category, name, defaultValue, comment).string
    }
}

class DoubleConfigurable(config: Configuration, category: String = Configuration.CATEGORY_GENERAL, name: String,
        defaultValue: Double, comment: String) : Configurable<Double>(config, category, name, defaultValue, comment) {
    override fun obtainValue(): Double {
        return config.get(category, name, defaultValue, comment).double
    }
}