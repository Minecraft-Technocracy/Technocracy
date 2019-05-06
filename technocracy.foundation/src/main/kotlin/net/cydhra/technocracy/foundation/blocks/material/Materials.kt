package net.cydhra.technocracy.foundation.blocks.material

import net.minecraft.block.material.MapColor
import net.minecraft.block.material.MaterialLiquid

val oilMaterial = object : MaterialLiquid(MapColor.BLACK_STAINED_HARDENED_CLAY) {
    override fun blocksMovement(): Boolean {
        return true
    }
}