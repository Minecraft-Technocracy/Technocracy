package net.cydhra.technocracy.foundation.model.items.api

import net.cydhra.technocracy.foundation.model.blocks.color.IBlockColor

/**
 * Common interface to all items in technocracy
 */
interface TCItem {

    val oreDictName: String?

    val itemColor: IBlockColor?

    val modelLocation: String
}