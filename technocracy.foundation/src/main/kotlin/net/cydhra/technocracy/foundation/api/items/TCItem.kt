package net.cydhra.technocracy.foundation.api.items

import net.cydhra.technocracy.foundation.api.blocks.color.IBlockColor

/**
 * Common interface to all items in technocracy
 */
interface TCItem {

    val oreDictName: String?

    val itemColor: IBlockColor?

    val modelLocation: String
}