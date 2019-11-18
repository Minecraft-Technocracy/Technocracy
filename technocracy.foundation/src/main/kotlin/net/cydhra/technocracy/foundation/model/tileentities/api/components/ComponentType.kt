package net.cydhra.technocracy.foundation.model.tileentities.api.components

enum class ComponentType(val supportsWaila: Boolean = false) {
    ENERGY(true),
    FLUID(true),
    INVENTORY(true),
    OPTIONAL(true),
    OTHER
}