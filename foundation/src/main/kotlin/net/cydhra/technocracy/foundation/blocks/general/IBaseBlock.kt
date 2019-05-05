package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor

/**
 * To simplify handling blocks within this modification, every block that is being added by this modification has to
 * implement this interface.
 */
interface IBaseBlock {

    /**
     * The location where to expect the model. For a class implementing a single block this can be defaulted to the
     * registry name. Only if multiple instances of the implementing class are being created, this should be changed
     * to something dynamic.
     */
    val modelLocation: String

    /**
     * To ease registration blocks define their own modifiers on block colors on implementation. [BlockManager] will
     * use this information during registration and no further steps are required. If no special block color is
     * required, this field is set to null.
     */
    val colorMultiplier: ConstantBlockColor?

    val generateItem: Boolean
}