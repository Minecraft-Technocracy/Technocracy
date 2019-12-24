package net.cydhra.technocracy.foundation.model.components

/**
 * Type of component. Those types are important to decide rendering of component contents in GUI and whether
 * meta-information should be made available through modifications like "Waila" or "TOP". Every system outside of the
 * components should rely on the type to defer information about the component's purpose and content.
 *
 * @param supportsWaila whether meta-information-displays like "Waila" should report about this component
 */
enum class ComponentType(val supportsWaila: Boolean = false) {
    /**
     * The component contains energy in any form
     */
    ENERGY(true),

    /**
     * The component contains fluids in any form
     */
    FLUID(true),

    /**
     * The component assigns an inventory to its composite
     */
    INVENTORY(true),

    /**
     * The component models that its content can be missing, based on a condition
     */
    OPTIONAL(true),

    /**
     * The component does not fall in any other category and is thus not handled specifically outside of its own
     * context.
     */
    OTHER
}