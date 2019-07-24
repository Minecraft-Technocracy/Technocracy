package net.cydhra.technocracy.foundation.config

import net.minecraftforge.common.config.Configuration
import kotlin.reflect.KProperty

/**
 * A [Configurable] is a delegate object for properties that contain a value obtained from configuration files. The
 * delegate will handle loading from config, caching of loaded values and reloading, if the config was changed. The
 * delegate can use the value transparently unknowing of details.
 *
 * ### Example:
 * ```Kotlin
 * val energyCost by Configurable(TCFoundations.config, "machines", "baseEnergyCost", 4, "Base energy cost per tick")
 *
 * // -- SNIP --
 *
 * fun doMachineWork() {
 *      // ...
 *      this.energyStorage.consume(this.energyCost)
 *      // ...
 * }
 * ```
 *
 * ### Parameters
 * @param config the configuration instance where to load the value from.
 * @param category the configuration category where the item is located in the config hierarchy
 * @param name name of the config option
 * @param defaultValue value that this config option takes, if no other value was provided by the config file
 * @param comment human readable comment that is assigned to the option within the file
 */
@Suppress("UNCHECKED_CAST")
abstract class Configurable<T : Any>(protected val config: Configuration,
                                     protected val category: String = Configuration.CATEGORY_GENERAL, protected val name: String,
                                     protected val defaultValue: T, protected val comment: String) {

    protected companion object {
        var cache = mutableMapOf<Pair<String, String>, Any>()
    }

    init {
        readValueFromConfig()
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T = readValueFromConfig()

    fun readValueFromConfig(): T {
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
                config.save()
            }
        }
    }

    /**
     * A strategy function that obtains the value from config. It must be redeclared in subclasses, as the code for
     * obtaining values is different for different content types [T]
     *
     * @return the value that is stored in the configuration
     */
    protected abstract fun obtainValue(): T
}

/**
 * A config option that stores an integer value.
 */
class IntegerConfigurable(config: Configuration, category: String = Configuration.CATEGORY_GENERAL, name: String,
                          defaultValue: Int, comment: String, private val minValue: Int, private val maxValue: Int) :
        Configurable<Int>(config, category, name, defaultValue, comment) {

    override fun obtainValue(): Int {
        return config.get(category, name, defaultValue, comment, minValue, maxValue).int
    }
}

/**
 * A config option that stores a boolean value.
 */
class BooleanConfigurable(config: Configuration, category: String = Configuration.CATEGORY_GENERAL, name: String,
                          defaultValue: Boolean, comment: String) : Configurable<Boolean>(config, category, name, defaultValue, comment) {
    override fun obtainValue(): Boolean {
        return config.get(category, name, defaultValue, comment).boolean
    }
}

/**
 * A config option that stores a string.
 */
class StringConfigurable(config: Configuration, category: String = Configuration.CATEGORY_GENERAL, name: String,
        defaultValue: String, comment: String) : Configurable<String>(config, category, name, defaultValue, comment) {
    override fun obtainValue(): String {
        return config.get(category, name, defaultValue, comment).string
    }
}

/**
 * A config option that stores a (double precision) floating point number. Note, that for reasons of consistency and
 * simplicity, no float config options are supported by this system.
 */
class DoubleConfigurable(config: Configuration, category: String = Configuration.CATEGORY_GENERAL, name: String,
                         defaultValue: Double, comment: String) : Configurable<Double>(config, category, name, defaultValue, comment) {
    override fun obtainValue(): Double {
        return config.get(category, name, defaultValue, comment).double
    }
}