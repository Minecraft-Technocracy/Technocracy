package net.cydhra.technocracy.foundation.util.propertys

import net.minecraftforge.common.property.IUnlistedProperty


class UnlistedProperty<T>(val propertyName: String, val valueClass: Class<T>) : IUnlistedProperty<T> {
    override fun valueToString(value: T): String {
        return value.toString()
    }

    override fun getName(): String {
        return propertyName
    }

    override fun getType(): Class<T> {
        return valueClass
    }

    override fun isValid(value: T?): Boolean {
        return value != null
    }
}