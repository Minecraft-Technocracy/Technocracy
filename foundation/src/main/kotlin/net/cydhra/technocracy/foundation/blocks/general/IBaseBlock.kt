package net.cydhra.technocracy.foundation.blocks.general

import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor

interface IBaseBlock {
    val modelLocation: String
    val colorMultiplier: ConstantBlockColor?
}